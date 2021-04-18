package com.example.clouddemocommon.redis;



/**锁接口
 * @author EDZ
 */
public interface ILock {

    /**
     * 持有锁的最长时间,300秒
     */
    long MAX_HOLD_SECONDS = 300L;

    /**
     * 获取锁
     *
     * @param key
     * @param maxWaitSeconds 等待锁的最长时间
     * @param salt      盐值，锁的内容,有一种情况:如果在获取锁之前先尝试解锁,如果不加salt,则可以直接解锁.可以传任何字符串
     * @return
     */
    boolean tryLock(String key, String salt, long maxWaitSeconds);

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    boolean unlock(String key, String saltValue);

}