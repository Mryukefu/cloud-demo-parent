package com.example.clouddemodata.controller;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.example.clouddemocommon.entry.vo.Redis1Dmo;
import com.example.clouddemocommon.entry.vo.RedisDmo;
import com.example.clouddemocommon.redis.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("test")
public class DbTestController {


    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping(value = "feigin")
    public String test() {
        RedisDmo redisDmo = new RedisDmo();
      /*  redisDmo.setAge(1).setHi(1L).setName("nihoa");
        cacheManager.set("test",redisDmo);*/
        RedisDmo test = (RedisDmo)cacheManager.get("test");

          return "hello-world";
    }




}
