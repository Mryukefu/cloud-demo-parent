package com.example.clouddemomq.constant;




/**
 * des 交换机 队列名称
 * 队列名称尽量按照rabbitMq 交换机名称规范来
 * [容器名称].[使用的平台名称].[ 业务名].[功能]
 * 详细 可以产考 https://www.cnblogs.com/csy8fs/p/13690133.html
 * @author yukefu
 * @date 2021/1/30 11:27
 */
public class ExchangeNameConst {


    public static final String TEST_DIRECT_EXCHANGE_1 = "TestDirectExchange1";

    public static final String TEST_DIRECT_EXCHANGE = "TestDirectExchange";
   /** 旧交换机*/
    public static final String TASK_DIRECT_EXCHANGE = "TaskDirectExchange";

}

