package com.example.clouddemodata.mapper;

import com.example.clouddemodata.entry.po.MqOperateRecordPO;
import tk.mybatis.mapper.common.BaseMapper;

public interface MqOperateRecordMapper extends BaseMapper<MqOperateRecordPO> {


    Integer updateHandleDataViaOptimisticLock(MqOperateRecordPO mqOperateRecordPO);
}
