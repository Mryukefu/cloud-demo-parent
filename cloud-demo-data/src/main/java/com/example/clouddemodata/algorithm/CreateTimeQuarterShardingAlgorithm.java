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
public class CreateTimeQuarterShardingAlgorithm implements PreciseShardingAlgorithm<Integer>{

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		return logicTableName +"_"+ LocalDate.now().getYear() + "_" + quarter(shardingValue.getValue());
	}

	private int quarter(Integer shardingValue){

		LocalDate localDate = Instant.ofEpochMilli(shardingValue*1000L).atZone(ZoneOffset.ofHours(0)).toLocalDate();
		int monthValue = localDate.getMonthValue();
		if (monthValue>=1&&monthValue<=3){
			return 1;
		}
		if (monthValue>=4&&monthValue<=6){
			return 2;
		}
		if (monthValue>=7&&monthValue<=9){
			return 3;
		}

		if (monthValue>=10&&monthValue<=12){
			return 4;
		}
		return 0;

	}


}
