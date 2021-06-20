package com.example.clouddemouser.controller;

import com.example.clouddemocommon.entry.constant.ExchangeNameConst;
import com.example.clouddemocommon.feign.TestFeign;
import com.wlwx.client.SmsClientWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("test/user")
public class TestController {

    @Autowired
    private TestFeign testFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SmsClientWrapper smsClientWrapper;

    @GetMapping
    public String test(){
        //return testFeign.test();
        Map<String, Integer> map = new HashMap<>();
        map.put("id",1);
        rabbitTemplate.convertAndSend(ExchangeNameConst.EX_FANOUT_TEST,"",map);
        return "ok";
    }
}
