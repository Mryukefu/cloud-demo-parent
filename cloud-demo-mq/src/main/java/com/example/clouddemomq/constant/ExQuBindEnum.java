package com.example.clouddemomq.constant;

import lombok.Getter;
import lombok.ToString;
import org.springframework.amqp.core.ExchangeTypes;


/**
 * des 绑定枚举类
 * 管理所有的绑定，配置
 * 交换机默认是选择直连模式（FANOUT） 默认是普通队列（非延迟）
 * 后面在需要增加队列的话，这边定义就OK了
 * @author yukefu
 * @date 2021/1/30 11:20
 */
@Getter
@ToString
public enum ExQuBindEnum {
    /**测试mq*/
    TEST_DIRECT_1(ExchangeTypes.FANOUT,
            ExchangeNameConst.TEST_DIRECT_EXCHANGE_1,
            QueueNameConstant.TEST_DIRECT_QUEUE_1,
            RouteKeyConst.TEST_DIRECT_ROUTING_1, false);


    /** 默认交换机类型是直连。不需要路由效率比较高,也可以是其他类型*/
    private String exchangeTypes = ExchangeTypes.FANOUT;
    /** 交换机名称*/
    private String exName;
    /** 队列名称*/
    private String mqName;
    /** routingKeyName*/
    private String keyName = "";
    /** 是否延迟队列 模式是延迟的*/
    private Boolean isDelayQueue = false;


    ExQuBindEnum(String exName, String mqName, String keyName) {
        this.exName = exName;
        this.mqName = mqName;
        this.keyName = keyName;
    }

    ExQuBindEnum(String exchangeTypes, String exName, String mqName, String keyName) {
        this.exchangeTypes = exchangeTypes;
        this.exName = exName;
        this.mqName = mqName;
        this.keyName = keyName;
    }

    /**
     * method desc
     * 构造方法 构造一个直接
     * @param exName 交换机名称，必填
     * @param mqName mq通道名称，必填
     * @param keyName 路由名称，可不填
     * @param isDelayQueue ture 延迟队列
     * @return {@code }
     * @author ykf
     * @date 2021/1/30 12:16
     *
     */

    ExQuBindEnum(String exName, String mqName, String keyName, boolean isDelayQueue) {
        this.exName = exName;
        this.mqName = mqName;
        this.keyName = keyName;
        this.isDelayQueue = isDelayQueue;
    }

    ExQuBindEnum(String exchangeTypes, String exName, String mqName, String keyName, Boolean isDelayQueue) {
        this.exchangeTypes = exchangeTypes;
        this.exName = exName;
        this.mqName = mqName;
        this.keyName = keyName;
        this.isDelayQueue = isDelayQueue;
    }


}