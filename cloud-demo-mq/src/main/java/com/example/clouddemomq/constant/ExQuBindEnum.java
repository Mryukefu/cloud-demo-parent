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
    TEST_DIRECT_1(ExchangeTypes.DIRECT, ExchangeNameConst.TEST_DIRECT_EXCHANGE_1, QueueNameConstant.TEST_DIRECT_QUEUE_1, RouteKeyConst.TEST_DIRECT_ROUTING_1, false),

    /**测试mq*/
    TEST_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TEST_DIRECT_EXCHANGE, QueueNameConstant.TEST_DIRECT_QUEUE, RouteKeyConst.TEST_DIRECT_ROUTING, false),
    /**测试mq*/
    TASK_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.TASK_DIRECT_QUEUE, RouteKeyConst.TASK_DIRECT_ROUTING, false),
    /**测试mq*/
    GUILD_DIRECT_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.GUILD_DIRECT_QUEUE, RouteKeyConst.GUILD_DIRECT_ROUTING, false),
    /**测试mq*/
    UPGRADE_DIRECT_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.UPGRADE_DIRECT_QUEUE, RouteKeyConst.UPGRADE_DIRECT_ROUTING, false),
    /**测试mq*/
    DISCOUNT_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.DISCOUNT_DIRECT_QUEUE, RouteKeyConst.DISCOUNT_DIRECT_QUEUEROUTING, false),
    /**测试mq*/
    AY_GAME_COIN_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.PAY_GAME_COIN_DIRECT_QUEUE, RouteKeyConst.PAY_GAME_COIN_DIRECT_ROUTING, false),
    /**测试mq*/
    VIP_ORDER_DIRECT_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.VIP_ORDER_DIRECT_QUEUE, RouteKeyConst.VIP_ORDER_DIRECT_ROUTING, false),
    /**测试mq*/
    INVITE_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.INVITE_DIRECT_QUEUE, RouteKeyConst.INVITE_DIRECT_ROUTING, false),
    /**测试mq*/
    NEW_USER_AWARD_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.NEW_USER_AWARD_DIRECT_QUEUE, RouteKeyConst.NEW_USER_AWARD_DIRECT_ROUTING, false),
    /**测试mq*/
    WELFARE_COIN_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.WELFARE_COIN_DIRECT_QUEUE, RouteKeyConst.WELFARE_COIN_DIRECT_ROUTING, false),
    /**测试mq*/
    MEMBER_INFO_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.MEMBER_INFO_DIRECT_QUEUE, RouteKeyConst.MEMBER_INFO_DIRECT_ROUTING, false),
    /**测试mq*/
    USER_INFO_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.USER_INFO_MODIFY_QUEUE, RouteKeyConst.USER_INFO_MODIFY, false),
    /**测试mq*/
    BT_REBATE_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.BT_REBATE_QUEUE, RouteKeyConst.BT_REBATE_ROUTING, false),
    /**测试mq*/
    BT_REBATE_APPLY_LOG_DIRECT(ExchangeTypes.DIRECT, ExchangeNameConst.TASK_DIRECT_EXCHANGE, QueueNameConstant.BT_REBATE_APPLY_QUEUE, RouteKeyConst.BT_REBATE_APPLY_LOG_ROUTING, false);

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