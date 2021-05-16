package com.example.clouddemodata.service.impl;

import com.example.clouddemodata.entry.po.MqOperateRecordPO;
import com.example.clouddemodata.mapper.MqOperateRecordMapper;
import com.example.clouddemodata.service.MqOperateRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MqOperateRecordServiceImpl implements MqOperateRecordService {

    @Autowired
    private MqOperateRecordMapper mqOperateRecordMapper;
    @Override
    public MqOperateRecordPO getMqOneRecode(Integer id) {

       // return mqOperateRecordMapper.selectById(id);
        return null;
    }

    @Override
    public Integer updateHandleDataViaOptimisticLock(MqOperateRecordPO mqOperateRecordPO) {

        return mqOperateRecordMapper.updateHandleDataViaOptimisticLock(mqOperateRecordPO);
    }
}
