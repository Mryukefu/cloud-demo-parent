package com.example.clouddemodata.controller;

import com.example.clouddemodata.entry.po.DcUser;
import com.example.clouddemodata.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("test/data")
public class DataTestController {

    @Autowired
    private UserMapper dcUserMapper;


    @GetMapping(value = "get/query")
    public DcUser getData(){
      return  dcUserMapper.selectByPrimaryKey(1);


    }

    @PostMapping(value = "add/user")
    public String data(){
        DcUser dcUser = dcUserMapper.selectByPrimaryKey(new Long(1));
        Random random = new Random();
        dcUser.setUserPhone(random.doubles().toString());
        dcUser.setUserName(random.doubles().toString());
        dcUserMapper.myInsert( dcUser);
         return "ok";

    }
}
