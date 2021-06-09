package com.example.clouddemomq.support;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.example.clouddemocommon.entry.vo.MqOperateRecordVO;
import com.example.clouddemocommon.feign.MqFeign;
import com.example.clouddemocommon.redis.ILock;
import com.example.clouddemocommon.redis.RedisEnum;
import com.example.clouddemocommon.utils.ObjectConvertUtil;
import com.example.clouddemocommon.utils.ValidationUtil;
import com.example.clouddemomq.constant.MqOperateRecordPO;
import com.rabbitmq.client.Channel;
//import com.rabbitmq.http.client.Client;
//import com.rabbitmq.http.client.domain.BindingInfo;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.BindingInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author 袁康云
 * @title AbstractRabbitSupport
 * @projectName vip_core
 * @date 2019/6/14 20:07
 */
@Slf4j
public abstract class AbstractRabbitSupport implements IRabbitSupport<MqOperateRecordPO> {
   @Autowired
    protected Client client;
    @Value("${spring.rabbitmq.virtual-host}")
    protected String virtualHost;
  //  @Autowired
  protected MqFeign mqFeign;
    @Autowired
    protected ILock distributeLock;
    @Autowired
    protected RabbitTemplate rabbitTemplate;
    /** 队列绑定关系缓存*/
    private static final Map<String, Set<String>> BINDING_INFO = new ConcurrentHashMap<>();

    /**
     * 监听队列
     * @param queueName
     * @param message mq消息
     * @param channel mq渠道
     * @param recordId 记录ID；若持久化到数据库则传对应ID，否则传null
     * @param errorLog 错误日志
     * @param coreHandle 核心操作，比如调用service层的统计方法
     */
    @Override
    public void listenUp(String queueName, Message message, Channel channel, Integer recordId, String errorLog, Consumer<MqOperateRecordPO> coreHandle) {
        MqOperateRecordPO operateRecord = instanceOperateRecordByIdWithLock(recordId);

        try {
            // 走消息体持久化到数据库流程
            if (recordId != null) {
                if (handleRecordFinishedState(operateRecord, queueName)) {
                    return;
                }
            }

            coreHandle.accept(operateRecord);
        } catch (Exception e) {
            if (operateRecord != null) {
                handleRecordErrorAndPersistentWithTryCatch(operateRecord, queueName, e);
            }

            log.error("[{}]message：{}", errorLog, message, e);
        } finally {
            if (operateRecord != null) {
                unLock(recordId);
            }

            basicAckWithTryCatch(message, channel);
        }
    }

    /**
     * 分布式锁解锁
     * @param recordId
     */
    @Override
    public void unLock(Integer recordId) {
        String key = RedisEnum.getByFormat(RedisEnum.DISTRIBUTE_LOCK_MQ_OPERATE_RECORD, recordId);
        distributeLock.unlock(key, key);
    }

    /**
     * 查询交换机与队列的绑定关系
     * @param virtualHost
     * @return
     */
    @Override
    public Map<String, Set<String>> bindingsInfo(String virtualHost) {
      List<BindingInfo> bindings = client.getBindings(virtualHost);
        if (BINDING_INFO.isEmpty()) {
            BINDING_INFO.putAll(bindings
                    .stream().collect(groupingBy(BindingInfo::getSource, mapping(BindingInfo::getDestination, toSet()))));
        }
        return BINDING_INFO;
    }

    /**
     * 根据message查询mq消息记录
     * 加分布式锁
     * 约定走数据库持久化流程的mq消息内容保存的是mq_operate_record表的主键
     * @param recordId
     * @return
     */
    @Override
    public MqOperateRecordPO instanceOperateRecordByIdWithLock(Integer recordId) {
        if (recordId == null) {
            return null;
        }

        String key = RedisEnum.getByFormat(RedisEnum.DISTRIBUTE_LOCK_MQ_OPERATE_RECORD, recordId);

        try {
            distributeLock.tryLock(key, key, RedisEnum.DISTRIBUTE_LOCK_MQ_OPERATE_RECORD.getExpireSeconds());
            JSONObject jsonObject = mqFeign.getMqOneRecode(recordId);
            MqOperateRecordPO operateRecord = jsonObject.getObject("date", MqOperateRecordPO.class);
            ValidationUtil.assertNotNull(operateRecord, "消息记录不存在");
            return operateRecord;
        } catch (Exception e) {
            distributeLock.unlock(key, key);
            log.error("[实例化MQ记录、加锁失败]recordId:{}", recordId, e);
        }

        return null;
    }

    /**
     * 组合方法：
     * - 赋值mq操作异常的堆栈信息
     * - 持久化mq操作记录异常信息
     * - try-catch
     * @param operateRecord mq操作记录对象
     * @param currentQueue 当前操作的队列
     * @param throwable 错误堆栈
     */
    @Override
    public void handleRecordErrorAndPersistentWithTryCatch(MqOperateRecordPO operateRecord, String currentQueue, Throwable throwable) {
        try {
            handleRecordError(operateRecord, throwable, currentQueue);
            persistentRecordViaOptimisticLock(operateRecord);
        } catch (Exception e) {
            // nothing to do
        }
    }

    /**
     * 修改消息完成状态（在业务代码执行之前，假使本次处理能成功）
     * - 查询交换机绑定的队列信息
     * - 判断当前队列是否被消费（若已经被消费则返回true、中断流程）
     * - 判断是否是所有的队列都被消费成功（若是则修改消费成功状态）
     * @param operateRecord 消息数据库持久对象
     * @param currentQueue 当前操作的队列
     * @return false:中断流程 true：继续往下执行
     */
    @Override
    public boolean handleRecordFinishedState(MqOperateRecordPO operateRecord, String currentQueue) {
        ValidationUtil.assertNotBlank(operateRecord.getExchangeName(), "交换机不存在");

        String consumeQueues = operateRecord.getConsumeQueues();
        Map<String, Boolean> consumedQueues = convertConsumeQueues(consumeQueues);

        // 判断是否重复消费
        if (consumedQueues.containsKey(currentQueue) && consumedQueues.get(currentQueue)) {
            return true;
        }

        // 假设队列处理成功
        consumedQueues.put(currentQueue, true);
        operateRecord.setConsumeQueues(ObjectConvertUtil.convertObject(consumedQueues,String.class,""));

        // 标识mq记录为处理成功
//        Set<String> finishedQueues = consumedQueues.keySet();
        Set<String> finishedQueues = consumedQueues.entrySet()
                .stream().filter(entry -> entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<String, Set<String>> bindingsInfo = bindingsInfo();
        Set<String> bindingQueues = bindingsInfo.get(operateRecord.getExchangeName());

        if (finishedQueues.containsAll(bindingQueues)) {
            operateRecord.setState(2);
        }

        return false;
    }

    /**
     * 将数据库中保存的队列消费信息转换为java对象
     * @param consumeQueues
     * @return
     */
    private Map<String, Boolean> convertConsumeQueues(String consumeQueues) {
        // key:queueName value:isHandleSuccess
        Map<String, Boolean> consumedQueues = new HashMap<>(10);
        if (StringUtils.isNotBlank(consumeQueues)) {
            return ObjectConvertUtil.readStringAsObjectByJackson(consumeQueues, Map.class, consumedQueues);
        }
        return consumedQueues;
    }

    /**
     * 通用的，赋值mq操作异常的堆栈信息
     * - 错误堆栈长度截断，限制长度为512
     * - 修改mq操作记录的状态为消费失败
     * @param operateRecord mq操作记录对象
     * @param throwable 错误堆栈
     * @param currentQueue 当前处理的队列
     */
    @Override
    public void handleRecordError(MqOperateRecordPO operateRecord, Throwable throwable, String currentQueue) {
        if (operateRecord != null) {
            Map<String, Boolean> consumeQueues = convertConsumeQueues(operateRecord.getConsumeQueues());
            consumeQueues.put(currentQueue, false);
            operateRecord.setConsumeQueues(ObjectConvertUtil.writeObjectAsStringByJackson(consumeQueues, null));
            // 处理失败状态
            operateRecord.setState(3);
            operateRecord.setErrorMsg(StringUtils.truncate(throwable.getMessage(), 512));
        }
    }

    /**
     * 使用乐观锁的形式持久化mq操作记录
     * @param operateRecord
     */
    @Override
    public void persistentRecordViaOptimisticLock(MqOperateRecordPO operateRecord) {
        log.info("[保存MQ操作记录]{}", operateRecord);
        MqOperateRecordVO mqOperateRecordVO = new MqOperateRecordVO();
        BeanUtils.copyProperties(operateRecord,mqOperateRecordVO );
        JSONObject jsonObject = mqFeign.updateHandleDataViaOptimisticLock(mqOperateRecordVO);
        ValidationUtil.assertTrue( jsonObject.getObject("data",Integer.class)>0, "MQ操作记录保存失败");
    }

    /**
     * 查询交换机与队列的绑定关系，默认虚拟主机
     * @return Map.key exchangeName,Set.key queueName
     */
    public Map<String, Set<String>> bindingsInfo() {
        return bindingsInfo(virtualHost);
    }

    /**
     * 确认消费消息
     * @param message
     * @param channel
     * @throws IOException
     */
    @Override
    public void basicAck(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 确认消费消息，并且捕获异常
     * @param message
     * @param channel
     */
    @Override
    public void basicAckWithTryCatch(Message message, Channel channel) {
        try {
            basicAck(message, channel);
        } catch (IOException e) {
            log.error("[确认消费消息异常]message：{}，channel：{}", message, channel);
        }
    }

}
