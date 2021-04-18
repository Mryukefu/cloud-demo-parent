package com.example.clouddemocommon.redis;

import lombok.Getter;

/**
 *
 * redis常量
 * rabbitmq 配置类
 * @author yukefu
 * @date 2021/1/30 11:20
 */
@Getter
public enum RedisEnum {
    /**
     *
     */

    DISTRIBUTE_LOCK_MQ_OPERATE_RECORD("game:distribute_lock:mq_operate_record:%s", 600, "分布式锁：mq操作记录，参数为mq_operate_record主键;过期时间实际为等待锁的时间");


    /** 缓存的键*/
    private String key;
    /** 过期的秒数*/
    private long expireSeconds;
    /** 描述*/
    private String desc;

    RedisEnum(String key, long expireSeconds, String desc) {
        this.key = key;
        this.expireSeconds = expireSeconds;
        this.desc = desc;
    }



    /**
     * 获取带格式的枚举
     * 相当于String.format(formatStr,params)
     * @param redisEnum
     * @param params
     * @return
     */
    public static String getByFormat(RedisEnum redisEnum, Object... params) {
        return String.format(redisEnum.key, params);
    }
}