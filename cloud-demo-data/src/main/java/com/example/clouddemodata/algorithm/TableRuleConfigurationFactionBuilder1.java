package com.example.clouddemodata.algorithm;

import com.example.clouddemocommon.utils.ValidationUtil;
import io.shardingsphere.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import io.shardingsphere.api.algorithm.sharding.hint.HintShardingAlgorithm;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.api.algorithm.sharding.standard.RangeShardingAlgorithm;
import io.shardingsphere.api.config.strategy.*;
import io.shardingsphere.core.routing.strategy.ShardingStrategy;
import io.shardingsphere.core.routing.strategy.complex.ComplexShardingStrategy;
import io.shardingsphere.core.routing.strategy.hint.HintShardingStrategy;
import io.shardingsphere.core.routing.strategy.inline.InlineShardingStrategy;
import io.shardingsphere.core.routing.strategy.standard.StandardShardingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

/**
 * 类的功能描述
 *  构建表规则
 * @author 自己姓名
 * @date 2021/4/12 19:54
 */
@Slf4j
public class TableRuleConfigurationFactionBuilder1 {


  public static ShardingStrategy buildShardingStrategyConfiguration(String classNameShardingStrategyConfigurationParam,
                                                                    String classNameShardingAlgorithmParam,
                                                                    String shardingColumn,
                                                                    String algorithmExpression)  {
      try {
          //  获取配置分表策略类对象
          Class classNameShardingStrategy = Class.forName(classNameShardingStrategyConfigurationParam);
          // 获取算法类对象
          Class classNameShardingAlgorithm = Class.forName(classNameShardingAlgorithmParam);

          if (StandardShardingStrategy.class.isAssignableFrom(classNameShardingStrategy)){

              if (RangeShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)
                      &&PreciseShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor constructor  = StandardShardingStrategyConfiguration.class.getConstructor(String.class,
                          PreciseShardingAlgorithm.class,RangeShardingAlgorithm.class);

                  StandardShardingStrategyConfiguration shardingStrategyConfiguration =  (StandardShardingStrategyConfiguration)constructor.newInstance(shardingColumn,
                          classNameShardingAlgorithm.newInstance(), classNameShardingAlgorithm.newInstance());
                  return new StandardShardingStrategy(shardingStrategyConfiguration);
              }

              if (RangeShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor constructor  = StandardShardingStrategyConfiguration.class.getConstructor(String.class,
                          PreciseShardingAlgorithm.class,RangeShardingAlgorithm.class);

                  StandardShardingStrategyConfiguration shardingStrategyConfiguration=   (StandardShardingStrategyConfiguration)constructor.newInstance(shardingColumn,
                         null, classNameShardingAlgorithm.newInstance());
                  return new StandardShardingStrategy(shardingStrategyConfiguration);
              }

              if (PreciseShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor constructor  = StandardShardingStrategyConfiguration.class.getConstructor(String.class,PreciseShardingAlgorithm.class);

                  StandardShardingStrategyConfiguration  shardingStrategyConfiguration =  (StandardShardingStrategyConfiguration)constructor.newInstance(shardingColumn,
                          classNameShardingAlgorithm.newInstance());
                  return new StandardShardingStrategy(shardingStrategyConfiguration);
              }


          }
          if (ComplexShardingStrategy.class.isAssignableFrom(classNameShardingStrategy)){
              if (ComplexKeysShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor   constructor  = ComplexShardingStrategyConfiguration.class.getConstructor(String.class,ComplexKeysShardingAlgorithm.class);
                  ComplexShardingStrategyConfiguration  shardingStrategyConfiguration =  (ComplexShardingStrategyConfiguration)constructor.newInstance(shardingColumn,
                          classNameShardingAlgorithm.newInstance(), classNameShardingAlgorithm.newInstance());
                  return new ComplexShardingStrategy(shardingStrategyConfiguration);
              }
          }

          if (HintShardingStrategy.class.isAssignableFrom(classNameShardingStrategy)){
              if (HintShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor constructor  = HintShardingStrategyConfiguration.class.getConstructor(String.class,HintShardingStrategyConfiguration.class);
                  HintShardingStrategyConfiguration    shardingStrategyConfiguration =  (HintShardingStrategyConfiguration)constructor.newInstance(shardingColumn,
                          classNameShardingAlgorithm.newInstance(), classNameShardingAlgorithm.newInstance());
                  return new HintShardingStrategy(shardingStrategyConfiguration);
              }
          }
          if (InlineShardingStrategy.class.isAssignableFrom(classNameShardingStrategy)){
              Constructor constructor  = InlineShardingStrategyConfiguration.class.getConstructor(String.class,String.class);
              InlineShardingStrategyConfiguration shardingStrategyConfiguration =  (InlineShardingStrategyConfiguration)constructor.newInstance(shardingColumn,
                      algorithmExpression);
              return new InlineShardingStrategy(shardingStrategyConfiguration);
          }

          ValidationUtil.assertTrue(false,"不支持这个分表类型");
         } catch (Exception e) {
          log.error("构建分表策略异常",e);
          ValidationUtil.assertTrue(false,"构建分表策略异常");
      }
       return null;
  }


}
