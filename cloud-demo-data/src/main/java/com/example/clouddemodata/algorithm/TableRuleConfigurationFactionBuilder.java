/*
package com.example.clouddemodata.algorithm;

import com.example.clouddemodata.entry.enumkey.TableRuleConfigurationEnum;
import com.example.clouddemocommon.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Constructor;

*/
/**
 * 类的功能描述
 *  构建表规则
 * @author 自己姓名
 * @date 2021/4/12 19:54
 *//*

@Slf4j
public class TableRuleConfigurationFactionBuilder {


  public static ShardingStrategyConfiguration buildShardingStrategyConfiguration(TableRuleConfigurationEnum tableRuleConfigurationEnum)  {
      try {
          //  获取配置分表策略类对象
          Class classNameShardingStrategyConfiguration = tableRuleConfigurationEnum.getClassNameShardingStrategyConfiguration();
          // 获取算法类对象
          Class classNameShardingAlgorithm = tableRuleConfigurationEnum.getClassNameShardingAlgorithm();

          if (StandardShardingStrategyConfiguration.class.isAssignableFrom(classNameShardingStrategyConfiguration)){

              if (RangeShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)
                      &&PreciseShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){

                  Constructor constructor  = classNameShardingStrategyConfiguration.getConstructor(String.class,
                          PreciseShardingAlgorithm.class,RangeShardingAlgorithm.class);

                  return  (ShardingStrategyConfiguration)constructor.newInstance(tableRuleConfigurationEnum.getShardingColumn(),
                          classNameShardingAlgorithm.newInstance(), classNameShardingAlgorithm.newInstance());
              }

              if (RangeShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor constructor  = classNameShardingStrategyConfiguration.getConstructor(String.class,
                          PreciseShardingAlgorithm.class,RangeShardingAlgorithm.class);

                  return  (ShardingStrategyConfiguration)constructor.newInstance(tableRuleConfigurationEnum.getShardingColumn(),
                         null, classNameShardingAlgorithm.newInstance());
              }

              if (PreciseShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor constructor  = classNameShardingStrategyConfiguration.getConstructor(String.class,PreciseShardingAlgorithm.class);

                  return  (ShardingStrategyConfiguration)constructor.newInstance(tableRuleConfigurationEnum.getShardingColumn(),
                          classNameShardingAlgorithm.newInstance());
              }


          }
          if (ComplexShardingStrategyConfiguration.class.isAssignableFrom(classNameShardingStrategyConfiguration)){
              if (ComplexKeysShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor   constructor  = classNameShardingStrategyConfiguration.getConstructor(String.class,ComplexKeysShardingAlgorithm.class);
                  return  (ShardingStrategyConfiguration)constructor.newInstance(tableRuleConfigurationEnum.getShardingColumn(),
                          classNameShardingAlgorithm.newInstance(), classNameShardingAlgorithm.newInstance());
              }
          }

          if (HintShardingStrategyConfiguration.class.isAssignableFrom(classNameShardingStrategyConfiguration)){
              if (HintShardingAlgorithm.class.isAssignableFrom(classNameShardingAlgorithm)){
                  Constructor constructor  = classNameShardingStrategyConfiguration.getConstructor(String.class,HintShardingStrategyConfiguration.class);
                  return  (ShardingStrategyConfiguration)constructor.newInstance(tableRuleConfigurationEnum.getShardingColumn(),
                          classNameShardingAlgorithm.newInstance(), classNameShardingAlgorithm.newInstance());
              }
          }
          if (InlineShardingStrategyConfiguration.class.isAssignableFrom(classNameShardingStrategyConfiguration)){
              Constructor constructor  = classNameShardingStrategyConfiguration.getConstructor(String.class,String.class);
              return  (ShardingStrategyConfiguration)constructor.newInstance(tableRuleConfigurationEnum.getShardingColumn(),
                      tableRuleConfigurationEnum.getAlgorithmExpression());
          }
          ValidationUtil.assertTrue(false,"不支持这个分表类型");
         } catch (Exception e) {
          log.error("构建分表策略异常",e);
          ValidationUtil.assertTrue(false,"构建分表策略异常");
      }
       return null;
  }


}
*/
