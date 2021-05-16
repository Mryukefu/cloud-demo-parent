package com.example.clouddemouser.controller;

import com.example.clouddemocommon.feign.TestFeign;
import com.wlwx.client.SmsClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/user")
public class TestController {

    @Autowired
    private TestFeign testFeign;

    @Autowired
    private SmsClientWrapper smsClientWrapper;

    @GetMapping
    public String test(){
        return testFeign.test();
    }
}
