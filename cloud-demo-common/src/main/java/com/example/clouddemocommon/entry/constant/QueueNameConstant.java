package com.example.clouddemocommon.entry.constant;

import lombok.Getter;

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

    public static final String QUEUE_FANOUT_TEST ="queue:fanout:test";
}