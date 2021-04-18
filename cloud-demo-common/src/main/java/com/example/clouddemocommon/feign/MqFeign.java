package com.example.clouddemocommon.feign;

import com.alibaba.fastjson.JSONObject;
import com.example.clouddemocommon.entry.vo.MqOperateRecordVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "cloud-demo-data")
@RequestMapping("mq/behavior")
public interface MqFeign {


    @GetMapping(value = "test/feigin")
    JSONObject test();

    @GetMapping(value = "getOne")
    JSONObject getMqOneRecode(@RequestParam(name = "id") Integer id );

    @PostMapping(value = "updateHandleDataViaOptimisticLock")
    JSONObject updateHandleDataViaOptimisticLock(@RequestBody MqOperateRecordVO mqOperateRecordPO);
}
