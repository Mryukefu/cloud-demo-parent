package com.example.clouddemocommon.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * http://www.importnew.com/27477.html
 * redis分布式锁
 * @author ykf
 */
@Component
public class RedisLock implements ILock {

    @Autowired
    private RedisTemplate redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Override
    public boolean tryLock(String key, String saltValue, long maxWaitSeconds)  {
        long sleepInterSeconds = 0L;
        long expire = maxWaitSeconds == 0 ? MAX_HOLD_SECONDS : maxWaitSeconds;
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, saltValue, expire, TimeUnit.SECONDS);
        boolean ret = result.booleanValue();
        //不管有多少个线程竞争,最终只会返回给一个为true
        while (!ret && maxWaitSeconds > 0) {
            ret = redisTemplate.opsForValue().setIfAbsent(key, saltValue, expire, TimeUnit.SECONDS);;
            if (!ret && maxWaitSeconds != sleepInterSeconds) {
                try {
                    Thread.sleep(1000L);
                }catch (Exception e){
                    //不处理
                }
                sleepInterSeconds ++;
            }
        }
        return ret;
    }

    @Override
    public boolean unlock(String key, String saltValue) {
        Object dbSaltObj = redisTemplate.getExpire(key);
        //根本就没加过锁
        if (dbSaltObj == null) {
            return true;
        }
        String dbSaltValue = (String) dbSaltObj;
        //不是同一个请求设的锁
        if (!dbSaltValue.equals(saltValue)) {
            return false;
        } else {
            redisTemplate.delete(key);
            return true;
        }
    }
}
