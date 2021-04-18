package com.example.clouddemodata.entry.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * MQ操作记录表
 * </p>
 *
 * @author zhangjie
 * @since 2019-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mq_operate_record")
public class MqOperateRecordPO extends Model<MqOperateRecordPO> {

private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发送方名称，填写项目名
     */
    @TableField("appName")
    private String appName;

    /**
     * 交换机名称
     */
    @TableField("exchangeName")
    private String exchangeName;

    /**
     * 路由键
     */
    @TableField("routingKey")
    private String routingKey;

    /**
     * 多个参数，需要类去接收，可序列化为json字符串
     */
    private String data;

    /**
     * 消费成功的队列名，适合一个交换机绑定多个队列的场景：queue_a,queue_b
     */
    @TableField("consumeQueues")
    private String consumeQueues;

    /**
     * 错误堆栈信息
     */
    @TableField("errorMsg")
    private String errorMsg;

    /**
     * 处理状态；1.未处理2.成功 3 失败
     */
    private Integer state;

    /**
     * 延迟队列预计处理时间：如当前时间为10:10，半小时后执行则应该保存为10:40对应的时间戳
     */
    @TableField("startDealTime")
    private Long startDealTime;

    /**
     * 添加时间
     */
    private Long ctime;

    /**
     * 备注
     */
    private String remark;

    /** 乐观锁版本号*/
    private int version;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
