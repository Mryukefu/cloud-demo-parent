package com.example.clouddemomq.constant;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;

/**
 * des rabbitMq 队列名称
 * 队列名称尽量按照rabbitMq 队列名称规范来
 * [容器名称].[使用的平台名称].[ 业务名].[功能]
 * 详细 可以产考 https://www.cnblogs.com/csy8fs/p/13690133.html
 * @author yukefu
 * @date 2021/1/30 11:27
 */
public class RouteKeyConst {

    public static final String TEST_DIRECT_ROUTING_1 = "TestDirectRouting_1";
    /**   */
    public static final String TEST_DIRECT_ROUTING = "TestDirectRouting";
    /**   */
    public static final String TASK_DIRECT_ROUTING = "TaskDirectRouting";
    /**   */
    public static final String GUILD_DIRECT_ROUTING = "GuildDirectRouting";
    /**   */
    public static final String UPGRADE_DIRECT_ROUTING = "UpgradeDirectRouting";
    /**   */
    public static final String ORDER_CANCEL_REMIND = "DiscountDirectQueueRouting";

    /**   */
    public static final String DISCOUNT_DIRECT_QUEUEROUTING = "key.order.cancel.delay";
    /**   */
    public static final String PAY_GAME_COIN_DIRECT_ROUTING = "PayGameCoinDirectRouting";

    /**   */
    public static final String VIP_ORDER_DIRECT_ROUTING = "VipOrderDirectRouting";
    /**   */
    public static final String INVITE_DIRECT_ROUTING = "InviteDirectRouting";

    /**   */
    public static final String NEW_USER_AWARD_DIRECT_ROUTING = "NewUserAwardDirectRouting";

    /**   */
    public static final String WELFARE_COIN_DIRECT_ROUTING = "WelfareCoinDirectRouting";
    /**   */
    public static final String MEMBER_INFO_DIRECT_ROUTING = "MemberInfoDirectRouting";

    /**   */
    public static final String USER_INFO_MODIFY = "UserInfoModify";

    /**   */
    public static final String BT_REBATE_ROUTING = "BtRebateRouting";

    /**   */
    public static final String BT_REBATE_APPLY_LOG_ROUTING = "BtRebateApplyLogRouting";


}