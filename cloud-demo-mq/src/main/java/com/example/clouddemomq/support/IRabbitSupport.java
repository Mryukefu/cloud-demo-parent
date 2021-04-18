package com.example.clouddemomq.support;

import com.example.clouddemomq.constant.MqOperateRecordPO;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * rabbit mq 操作支持接口
 * @author 袁康云
 * @title IRabbitSupport
 * @projectName vip_core
 * @date 2019/6/14 20:06
 */
public interface IRabbitSupport<T> {
    /**
     * 监听队列
     * @param queueName
     * @param message mq消息
     * @param channel mq渠道
     * @param recordId 记录ID；若持久化到数据库则传对应ID，否则传null
     * @param errorLog 错误日志
     * @param coreHandle 核心操作，比如调用service层的统计方法
     */
    void listenUp(String queueName, Message message, Channel channel, Integer recordId, String errorLog, Consumer<MqOperateRecordPO> coreHandle);

    /**
     * 查询交换机与队列的绑定关系
     * @param virtualHost
     * @return
     */
    Map<String, Set<String>> bindingsInfo(String virtualHost);

    /**
     * 根据message查询mq消息记录
     * 加分布式锁
     * 约定走数据库持久化流程的mq消息内容保存的是mq_operate_record表的主键
     * @param recordId
     * @return
     */
    T instanceOperateRecordByIdWithLock(Integer recordId);

    /**
     * 分布式锁解锁
     * @param recordId
     */
    void unLock(Integer recordId);

    /**
     * 修改消息完成状态（在业务代码执行之前，假使本次处理能成功）
     * - 查询交换机绑定的队列信息
     * - 判断当前队列是否被消费（若已经被消费则抛异常、中断流程）
     * - 判断是否是所有的队列都被消费成功（若是则修改消费成功状态）
     * @param operateRecord 消息数据库持久对象
     * @param currentQueue 当前操作的队列
     * @return false:中断流程 true：继续往下执行
     */
    boolean handleRecordFinishedState(T operateRecord, String currentQueue);

    /**
     * 通用的，赋值mq操作异常的堆栈信息
     * - 错误堆栈长度截断，限制长度为512
     * - 修改mq操作记录的状态为消费失败
     * @param operateRecord mq操作记录对象
     * @param throwable 错误堆栈
     * @param currentQueue 当前处理的队列
     */
    void handleRecordError(T operateRecord, Throwable throwable, String currentQueue);

    /**
     * 使用乐观锁的形式持久化mq操作记录
     * @param operateRecord
     */
    void persistentRecordViaOptimisticLock(T operateRecord);

    /**
     * 组合方法：
     * - 赋值mq操作异常的堆栈信息
     * - 持久化mq操作记录异常信息
     * - try-catch
     * @param operateRecord mq操作记录对象
     * @param currentQueue 当前操作的队列
     * @param throwable 错误堆栈
     */
    void handleRecordErrorAndPersistentWithTryCatch(T operateRecord, String currentQueue, Throwable throwable);

    /**
     * 确认消费消息
     * @param message
     * @param channel
     * @throws IOException
     */
    void basicAck(Message message, Channel channel) throws IOException;

    /**
     * 确认消费消息，捕获异常
     * @param message
     * @param channel
     */
    void basicAckWithTryCatch(Message message, Channel channel);
}
