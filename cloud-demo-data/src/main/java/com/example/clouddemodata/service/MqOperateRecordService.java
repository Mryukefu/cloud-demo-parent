package com.example.clouddemodata.service;

import com.example.clouddemodata.entry.po.MqOperateRecordPO;

public interface MqOperateRecordService {


    MqOperateRecordPO getMqOneRecode(Integer id);

    Integer updateHandleDataViaOptimisticLock(MqOperateRecordPO mqOperateRecordPO);

}
