package com.example.clouddemodata.algorithm;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

/**
 * 按照主键分表
 * @author ykf
 * @since 2020/11/19
 *
 */
public class CreateFieldShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
        Integer value = shardingValue.getValue();
        String logicTableName = shardingValue.getLogicTableName();
        return logicTableName+"_"+value%10;
    }


}
