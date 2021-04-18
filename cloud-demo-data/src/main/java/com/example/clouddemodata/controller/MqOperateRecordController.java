package com.example.clouddemodata.controller;

import com.example.clouddemodata.entry.po.MqOperateRecordPO;
import com.example.clouddemodata.service.MqOperateRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("mq/behavior")
public class MqOperateRecordController {

    @Autowired
    private MqOperateRecordService mqOperateRecordService;


    @GetMapping(value = "getOne")
    public MqOperateRecordPO getMqOneRecode(@RequestParam(name = "id") Integer id){
       return mqOperateRecordService.getMqOneRecode(id);
    }


    @PostMapping(value = "updateHandleDataViaOptimisticLock")
    public Integer updateHandleDataViaOptimisticLock(@RequestBody MqOperateRecordPO mqOperateRecordPO){
        return mqOperateRecordService.updateHandleDataViaOptimisticLock(mqOperateRecordPO);
    }
}
