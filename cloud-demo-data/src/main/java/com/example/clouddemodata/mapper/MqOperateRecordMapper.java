package com.example.clouddemodata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clouddemodata.entry.po.MqOperateRecordPO;

public interface MqOperateRecordMapper  extends BaseMapper<MqOperateRecordPO> {


    Integer updateHandleDataViaOptimisticLock(MqOperateRecordPO mqOperateRecordPO);
}
