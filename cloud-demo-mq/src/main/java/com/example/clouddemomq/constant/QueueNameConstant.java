package com.example.clouddemomq.constant;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

/**
 * des rabbitMq 队列名称
 * 队列名称尽量按照rabbitMq 队列名称规范来
 * [容器名称].[使用的平台名称].[ 业务名].[功能]
 * 详细 可以产考 https://www.cnblogs.com/csy8fs/p/13690133.html
 * @author yukefu
 * @date 2021/1/30 11:27
 */
@Getter
public class QueueNameConstant {

    public static final String TEST_DIRECT_QUEUE_1 ="TestDirectQueue1";
    /** */
    public static final String TEST_DIRECT_QUEUE ="TestDirectQueue";

    /** */
    public static final String TASK_DIRECT_QUEUE ="TaskDirectQueue";

    /** */
    public static final String GUILD_DIRECT_QUEUE ="GuildDirectQueue";

    /** */
    public static final String UPGRADE_DIRECT_QUEUE ="UpgradeDirectQueue";

    /** */
    public static final String PAY_GAME_COIN_DIRECT_QUEUE ="PayGameCoinDirectQueue";

    /** */
    public static final String VIP_ORDER_DIRECT_QUEUE ="VipOrderDirectQueue";

    /** */
    public static final String DISCOUNT_DIRECT_QUEUE ="DiscountDirectQueue";

    /** */
    public static final String INVITE_DIRECT_QUEUE ="InviteDirectQueue";

    /** */
    public static final String NEW_USER_AWARD_DIRECT_QUEUE ="NewUserAwardDirectQueue";

    /** */
    public static final String WELFARE_COIN_DIRECT_QUEUE ="WelfareCoinDirectQueue";

    /** */
    public static final String MEMBER_INFO_DIRECT_QUEUE ="MemberInfoDirectQueue";

    /** */
    public static final String USER_INFO_MODIFY_QUEUE ="UserInfoModifyQueue";

    /** */
    public static final String BT_REBATE_QUEUE ="BtRebateQueue";

    /** */
    public static final String BT_REBATE_APPLY_QUEUE ="BtRebateApplyQueue";



}