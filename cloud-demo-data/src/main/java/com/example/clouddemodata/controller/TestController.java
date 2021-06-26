package com.example.clouddemodata.controller;

import com.dc.game.shardingspringbootstarter.entry.po.DcUser;
import com.example.clouddemodata.mapper.UserMapper;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * class desc
 * todo
 *
 * @author ykf
 * @date 2021/6/25 16:23
 */
@RestController
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("test")
    public List<DcUser> test() {
       return Arrays.asList(userMapper.selectByPrimaryKey(1L)) ;


    }

    @GetMapping("test2")
    public Integer test2() {
        DcUser dcUser = userMapper.selectByPrimaryKey(10L);
        dcUser.setUserId(606882661012602880L);
        dcUser.setUserName(RandomUtils.nextFloat()+"");
        dcUser.setUserPhone(RandomUtils.nextFloat()+"");
        dcUser.setUserPassword(RandomUtils.nextFloat()+"");
        DcUser dcUser1 = new DcUser();
        BeanUtils.copyProperties(dcUser, dcUser1);
        dcUser1.setUserId(606882661012602881L);
        dcUser.setUserName(RandomUtils.nextFloat()+"");
        dcUser.setUserPhone(RandomUtils.nextFloat()+"");
        dcUser.setUserPassword(RandomUtils.nextFloat()+"");
        List<DcUser> list = new ArrayList<>();
        list.add(dcUser);
        list.add(dcUser1);
        for (DcUser user : list) {
            userMapper.insertSelective(user);
        }
        return 1;

    }
}
