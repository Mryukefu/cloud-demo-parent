package com.example.clouddemodata.algorithm;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;


/**
 * 按天分片规则
 * @author wuhongchao
 * @since 2020/11/19
 *
 */
public class CreateTimeYearShardingAlgorithm implements PreciseShardingAlgorithm<Integer>{

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		return logicTableName +"_" + year(shardingValue.getValue());
	}

	private int year(Integer shardingValue){

		LocalDate localDate = Instant.ofEpochMilli(shardingValue*1000L).atZone(ZoneOffset.ofHours(0)).toLocalDate();
		return localDate.getYear();

	}


}
