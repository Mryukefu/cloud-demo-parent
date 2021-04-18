package com.example.clouddemodata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class DataTestController {

    @GetMapping(value = "feigin")
    public String test(){
        return "hello-world";
    }
}
