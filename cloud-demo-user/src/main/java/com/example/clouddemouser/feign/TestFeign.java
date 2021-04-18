package com.example.clouddemouser.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "cloud-demo-data")
public interface TestFeign {


    @GetMapping(value = "test/feigin")
    String test();
}
