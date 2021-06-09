package com.example.clouddemodata.event;
import com.example.clouddemodata.config.TableRuleConfigurationFactionBuilder;
import com.example.clouddemodata.entry.po.DbDataNodes;
import com.google.common.collect.Maps;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.rule.DataNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 刷新表结构
 * @author ykf
 * @since 2021/4/14
 *
 */
@Component
@EnableScheduling
@Order(9999)
public class ActualTableRuleRefreshFromDbEvent implements InitializingBean {
	
	@Autowired
    private DataSource dataSource;
	/**
	 *
	 * 表节点刷新调度
	 * @param
	 * @return {@code void}
	 * @author ykf
	 * @date 2021/4/16 17:16
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void actualDataNodesRefresh() throws NoSuchFieldException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		ShardingDataSource shardingDataSource = (ShardingDataSource) dataSource;
		ShardingRule shardingRule = shardingDataSource.getRuntimeContext().getRule();
		ShardingConnection connection = shardingDataSource.getConnection();
		// 查询数据库配置的分表节点
		List<DbDataNodes> dbDataNodes = queryCreateTable(connection);
		// 封装实际表(包含数据库节点 dbName.loginTableName+后缀)
		List<DbDataNodes> calculateRequiredTables = calculateRequiredTableNames(dbDataNodes);

		Collection<TableRule> newTableRules  = structureNewTableRule(calculateRequiredTables);

		Field actualDataNodesField = ShardingRule.class.getDeclaredField("tableRules");
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(actualDataNodesField, actualDataNodesField.getModifiers() & Modifier.FINAL);
		actualDataNodesField.setAccessible(true);
		actualDataNodesField.set(shardingRule, newTableRules);


		List<String> existedTableNames = getExistedTableNames(dbDataNodes, connection);

		calculateRequiredTables = calculateRequiredTables.stream()
				.filter(t1 -> !existedTableNames.contains(t1.getAuthenticTableName()))
				.collect(Collectors.toList());

		if (calculateRequiredTables != null && calculateRequiredTables.size() > 0) {
			createTable(calculateRequiredTables);
		}


	}


	/**
	 *
	 * desc 创建实际上的节点，样式 dbName.tableName
	 * @param calculateRequiredTables  数据库配置的分表信息
	 * @return {@code java.util.Collection<io.shardingsphere.core.rule.TableRule>}
	 * @author ykf
	 * @date 2021/4/16 17:17
	 */
	private Collection<TableRule> structureNewTableRule(List<DbDataNodes> calculateRequiredTables)
			throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		List<TableRule> tableRules = new ArrayList<>();

		//  根据逻辑表进行分组
		Map<String, List<DbDataNodes>> map = calculateRequiredTables.stream()
				.collect(Collectors.groupingBy(DbDataNodes::getLogicTableName));

		for (String logicTableName : map.keySet()) {
			List<DbDataNodes> dbDataNodes = map.get(logicTableName);

			//  数据库规则
			TableRule tableRule = new TableRule("",logicTableName);

			//  设置新节点
			List<DataNode> newDataNodes = new ArrayList<>();

			//  设计数据库索引
			Map<DataNode, Integer> dataNodeIndexMap = Maps.newHashMap();
			AtomicInteger index = new AtomicInteger(0);

			//  设置数据库对应的表结构
			Map<String, List<String>> dataTableMap = new LinkedHashMap<>();
				List<String> tableName = new ArrayList<>();
			for (DbDataNodes dbDataNode : dbDataNodes) {
				DataNode dataNode = new DataNode(dbDataNode.getAuthenticTableName());
				dataNodeIndexMap.put(dataNode, index.intValue());
				index.incrementAndGet();
				tableName.add(dataNode.getTableName());
				newDataNodes.add(dataNode);
			}

			DbDataNodes dbDataNodes1 = dbDataNodes.get(0);

			dataTableMap.put(dbDataNodes1.getDateSourceName(),tableName);
			//  获取修改权限
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			// 反射刷新actualDataNodes
			Field actualDatasourceNamesField = TableRule.class.getDeclaredField("actualDatasourceNames");
			actualDatasourceNamesField.setAccessible(true);
			modifiersField.setInt(actualDatasourceNamesField, actualDatasourceNamesField.getModifiers() & Modifier.FINAL);
			actualDatasourceNamesField.set(tableRule, dbDataNodes.stream().map(DbDataNodes::getDateSourceName).collect(Collectors.toSet()));


			// 反射刷新actualDataNodes
			Field actualDataNodesField = TableRule.class.getDeclaredField("actualDataNodes");
			actualDataNodesField.setAccessible(true);
			modifiersField.setInt(actualDataNodesField, actualDataNodesField.getModifiers() & Modifier.FINAL);
			actualDataNodesField.set(tableRule, newDataNodes);

			// 反射刷新dataNodeIndexMap
			Field dataNodeIndexMapField = TableRule.class.getDeclaredField("dataNodeIndexMap");
			dataNodeIndexMapField.setAccessible(true);
			modifiersField.setInt(dataNodeIndexMapField, dataNodeIndexMapField.getModifiers() & Modifier.FINAL);
			dataNodeIndexMapField.set(tableRule, dataNodeIndexMap);

			// 反射刷新 tableShardingStrategy
			Field tableShardingStrategyField = TableRule.class.getDeclaredField("tableShardingStrategy");
			tableShardingStrategyField.setAccessible(true);
			modifiersField.setInt(tableShardingStrategyField, tableShardingStrategyField.getModifiers() & Modifier.FINAL);
			tableShardingStrategyField.set(tableRule, TableRuleConfigurationFactionBuilder
					.buildShardingStrategyConfiguration(dbDataNodes1.getClassNameShardingStrategy(),
							dbDataNodes1.getClassNameShardingAlgorithm(),dbDataNodes1.getShardingColumns(),null));

			// 反射刷新  generateKeyColumn
			Field generateKeyColumnField = TableRule.class.getDeclaredField("generateKeyColumn");
			generateKeyColumnField.setAccessible(true);
			modifiersField.setInt(generateKeyColumnField, generateKeyColumnField.getModifiers() & Modifier.FINAL);
			generateKeyColumnField.set(tableRule, dbDataNodes1.getGeneratorColumnName());


			// 反射刷新  shardingKeyGenerator
			Field shardingKeyGeneratorField = TableRule.class.getDeclaredField("shardingKeyGenerator");
			shardingKeyGeneratorField.setAccessible(true);
			modifiersField.setInt(shardingKeyGeneratorField, shardingKeyGeneratorField.getModifiers() & Modifier.FINAL);
			shardingKeyGeneratorField.set(tableRule, Class.forName(dbDataNodes1.getKeyGenerator()).newInstance());


			// 反射刷新  datasourceToTablesMap
			Field datasourceToTablesMapField = TableRule.class.getDeclaredField("datasourceToTablesMap");
			datasourceToTablesMapField.setAccessible(true);
			modifiersField.setInt(datasourceToTablesMapField, datasourceToTablesMapField.getModifiers() & Modifier.FINAL);
			datasourceToTablesMapField.set(tableRule, dataTableMap);
			tableRules.add(tableRule);

		}
		return tableRules;

	}

	/**
	 *
	 * 项目启动的时候创建分表信息，且刷新节点
	 * @param
	 * @return {@code void}
	 * @author ykf
	 * @date 2021/4/16 17:18
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// 查询需要的表
		Connection connection = dataSource.getConnection();
		List<DbDataNodes> list = queryCreateTable(connection);
		//
		List<DbDataNodes> calculateRequiredTables =  calculateRequiredTableNames(list);

		List<String> existedTableNames = getExistedTableNames(list,connection);
		calculateRequiredTables = calculateRequiredTables.stream()
				.filter(t1->!existedTableNames.contains(t1.getAuthenticTableName()))
				.collect(Collectors.toList());

		createTable(calculateRequiredTables);

		actualDataNodesRefresh();
	}
	
	/**
	 *
	 * 创建表
	 * @param tableNames
	 * @return {@code void}
	 * @author ykf
	 * @date 2021/4/16 16:17
	 */
	private void createTable(List<DbDataNodes> tableNames) {
		try (Connection connection = dataSource.getConnection();Statement statement = connection.createStatement()) {
			for (DbDataNodes tableName : tableNames) {
				statement.executeUpdate(tableName.getCreateTableTemplate());
			}
		} catch (Exception e) {
        	e.printStackTrace();
        }
	}
	

	/**
	 *
	 * 查询已存在的表（这个已经存在的表是数据库生成的表）
	 * @param list  数据库列表，
	 * @param connection
	 * @return {@code java.util.List<java.lang.String>}
	 * @author ykf
	 * @date 2021/4/16 17:19
	 */
	private List<String> getExistedTableNames(List<DbDataNodes> list,Connection connection) {
		List<String> result = new ArrayList<>();
        try {
			DatabaseMetaData metaData = connection.getMetaData();
		//	String dbName = connection.getCatalog();
			for (DbDataNodes dbDataNodes : list) {
				ResultSet tables = metaData.getTables(dbDataNodes.getDateSourceName(), null,
						dbDataNodes.getLogicTableName() + "%", new String[] { "TABLE" });
				while (tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					result.add("study"+"."+tableName);
				}
			}

        } catch (Exception e) {
        	e.printStackTrace();
        }
        return result;
	}


	/**
	 *
	 * 查询数据库创建的分表信息
	 * @param connection
	 * @return {@code java.util.List<com.project.common.entity.po.DbDataNodes>}
	 * @author ykf
	 * @date 2021/4/16 17:21
	 */
	private List<DbDataNodes> queryCreateTable(Connection connection)  {
		List<DbDataNodes> list = new ArrayList<>();
		//  查询需要创建的表
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM  db_data_nodes  where  state = 1");
			while(resultSet.next()){
				DbDataNodes dbDataNodes = new DbDataNodes();
				dbDataNodes.setDateSourceName(resultSet.getString("date_source_name"));
				dbDataNodes.setLogicTableName(resultSet.getString("logic_table_name"));
				dbDataNodes.setCreateTableTemplate(resultSet.getString("create_table_template"));
				dbDataNodes.setCreateNum(resultSet.getInt("create_num"));
				dbDataNodes.setType(resultSet.getInt("type") );
				dbDataNodes.setExpression(resultSet.getString("expression"));
				dbDataNodes.setAlgorithmExpression(resultSet.getString("algorithm_expression"));
				dbDataNodes.setClassNameShardingStrategy(resultSet.getString("class_name_sharding_strategy"));
				dbDataNodes.setClassNameShardingAlgorithm(resultSet.getString("class_name_sharding_algorithm"));
				dbDataNodes.setGeneratorColumnName(resultSet.getString("generator_column_name"));
				dbDataNodes.setShardingColumns(resultSet.getString("sharding_columns"));
				dbDataNodes.setKeyGenerator(resultSet.getString("key_generator"));
				dbDataNodes.setState(resultSet.getInt("state"));
				list.add(dbDataNodes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}


	/**
	 * 计算所需要的表
	 * @param
	 * @return {@code java.util.List<java.lang.String>}
	 * @author ykf
	 * @date 2021/4/13 19:47
	 */
	private List<DbDataNodes> calculateRequiredTableNames(List<DbDataNodes> list) {
		List<DbDataNodes> result = new ArrayList<>();
		//  查询需要创建的表

		for (DbDataNodes dbDataNodes : list) {
			StringBuilder stringBuilder = new StringBuilder().append(dbDataNodes.getDateSourceName()).append(".");
			stringBuilder.append(dbDataNodes.getLogicTableName());
			final int length = stringBuilder.length();
			Integer createNum = dbDataNodes.getCreateNum();
			for (Integer i = 0; i < createNum; i++) {
				DbDataNodes newDataNote = new DbDataNodes();
				BeanUtils.copyProperties(dbDataNodes, newDataNote);
				stringBuilder.setLength(length);
				//  按照年
				LocalDate now = LocalDate.now();
				if (dbDataNodes.getType() == 1) {
					stringBuilder.append("_").append(now.getYear()+ + i);
				}
				// 按季度
				if (dbDataNodes.getType() == 2) {
					String quarter = now.getYear() + "_" + (now.getMonthValue()/3 + (now.getMonthValue() % 3 > 0 ? 1 : 0)+i);
					stringBuilder.append("_").append(quarter);
				}
				//  按照月份
				if (dbDataNodes.getType() == 3) {
					String month = "_"+now.getYear() + "_" + (now.getMonthValue()+i);
					stringBuilder.append(month);
				}
				//  按照主键
				if (dbDataNodes.getType() == 4) {
					stringBuilder.append("_"+i);
				}
				newDataNote.setAuthenticTableName(stringBuilder.toString());
				newDataNote.setCreateTableTemplate(String.format(newDataNote.getCreateTableTemplate(), stringBuilder.toString()));
				result.add(newDataNote);
			}
		}
		return result;
	}


}
