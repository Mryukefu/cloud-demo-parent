package com.example.clouddemodata.config;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.example.clouddemocommon.utils.ValidationUtil;
import com.example.clouddemodata.algorithm.CreateFieldShardingAlgorithm;
import com.example.clouddemodata.entry.enumkey.BindingTableGroupsEnum;
import com.example.clouddemodata.entry.enumkey.TableRuleConfigurationEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
@EnableConfigurationProperties({DsProps.class})
@MapperScan(basePackages = {"com.example.clouddemodata.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory",
        sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceShardingConfig {

    @Bean
    public Filter statFilter() {
        StatFilter filter = new StatFilter();
        filter.setSlowSqlMillis(5000);
        filter.setLogSlowSql(true);
        filter.setMergeSql(true);
        return filter;
    }


    /**
     *
     * 这个是Druid监控
     * @param
     * @return {@code org.springframework.boot.web.servlet.ServletRegistrationBean}
     * @author ykf
     * @date 2021/4/12 10:51
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        //创建servlet注册实体
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //设置ip白名单
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        //设置控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        //是否可以重置数据
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }


    /**
     *
     * 后期如果需要使用多数据源的话可以手动放入ioc 容器
     * @param dsProp
     * @return {@code javax.sql.DataSource}
     * @author ykf
     * @date 2021/4/12 10:49
     */
    public DataSource ds0(@NotNull DsProps.DsProp dsProp) {
        Map<String, Object> dsMap = new HashMap<>();
        ValidationUtil.assertNotNull(dsProp.getType(),"没有配置数据源类型");
        ValidationUtil.assertNotNull(dsProp.getUrl(),"没有配置数据urL");
        ValidationUtil.assertNotNull(dsProp.getUsername(),"没有配置用户");
        ValidationUtil.assertNotNull(dsProp.getPassword(),"没有配置密码");
        dsMap.put("type",  dsProp.getType());
        dsMap.put("url", dsProp.getUrl());
        dsMap.put("username", dsProp.getUsername());
        dsMap.put("password", dsProp.getPassword());

        DruidDataSource ds = (DruidDataSource) DataSourceUtil.buildDataSource(dsMap);
        ds.setProxyFilters(Lists.newArrayList(statFilter()));
        // 每个分区最大的连接数
        ds.setMaxActive(20);
        // 每个分区最小的连接数
        ds.setMinIdle(5);
        return ds;
    }



    /**
     *
     * 获取sharding 数据源组
     * @param dsProps
     * @return {@code javax.sql.DataSource}
     * @author ykf
     * @date 2021/4/12 10:44
     */
    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DsProps dsProps) throws SQLException {
        //规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        List<DsProps.DsProp> ds = dsProps.getDs();
        ValidationUtil.assertNotNull(ds, "没有配置数据库");

        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (DsProps.DsProp dsProp : ds) {
            log.info("[数据源加载]{}", dsProp.getDcName());
            dataSourceMap.put(dsProp.getDcName(), ds0(dsProp));

            List<TableRuleConfigurationEnum> tableRuleConfigurationEnums = TableRuleConfigurationEnum
                    .TableRuleConfigurationEnum(dsProp.getDcName());
            if (tableRuleConfigurationEnums !=null&&tableRuleConfigurationEnums.size()>0){
                tableRuleConfigurationEnums.forEach(
                        dbNameEnums->{
                            shardingRuleConfig.getTableRuleConfigs().add(ruleConfig(dbNameEnums));
                        }
                );
            }
            // 配置分组规则
            List<String> tableGroups = BindingTableGroupsEnum.getTableByAdminType("ADMIN_DB");
            if (tableGroups != null) {
                for (String bindingTable : tableGroups) {
                    shardingRuleConfig.getBindingTableGroups().add(bindingTable);
                }
            }
        }

        // 获取数据源对象
        Properties p = new Properties();
        p.setProperty("sql.show", Boolean.TRUE.toString());
            DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig,p);
            return dataSource;
        }


    /**
     *
     * 设置表规则
     * @param tableRuleConfigurationEnum
     * @return {@code io.shardingsphere.api.config.rule.TableRuleConfiguration} 配置分表规则
     * @author ykf
     * @date 2021/4/12 10:54
     */

    private TableRuleConfiguration ruleConfig(TableRuleConfigurationEnum tableRuleConfigurationEnum) {
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getLogicTable(),"没有配置逻辑表");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getDbName(),"没有配置数据源名称");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getExpression(),"没有配置表达式");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getGeneratorColumnName(),"没有指明主键");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getShardingColumn(),"没有配置分片键");

        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration(tableRuleConfigurationEnum.getLogicTable(),tableRuleConfigurationEnum.getDbName()+
                "."+tableRuleConfigurationEnum.getLogicTable()+tableRuleConfigurationEnum.getExpression());
        tableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE",tableRuleConfigurationEnum.getGeneratorColumnName()) );

        tableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration
                (tableRuleConfigurationEnum.getShardingColumn(),
                new CreateFieldShardingAlgorithm()));
        return tableRuleConfig;
    }

        /**
         *
         * 需要手动配置事务管理器
         * @param dataSource
         * @return {@code org.springframework.jdbc.datasource.DataSourceTransactionManager}
         * @author ykf
         * @date 2021/4/12 10:55
         */
        @Bean
        public DataSourceTransactionManager transactitonManager (@Qualifier("dataSource") DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }

        /**
         *
         * mybatis 使用这个sqlSessionFactory
         * @param dataSource
         * @return {@code org.apache.ibatis.session.SqlSessionFactory}
         * @author ykf
         * @date 2021/4/12 10:55
         */
        @Bean("sqlSessionFactory")
        @Primary
        public SqlSessionFactory sqlSessionFactory (@Qualifier("dataSource") DataSource dataSource) throws Exception {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();

            bean.setDataSource(dataSource);
            bean.setTypeAliasesPackage("com.example.clouddemocommon.entry.po");

            bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:mappers/*.xml"));

            SqlSessionFactory sqlSessionFactory = bean.getObject();
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            configuration.setMapUnderscoreToCamelCase(true);
            return sqlSessionFactory;
        }


        @Bean("sqlSessionTemplate")
        @Primary
        public SqlSessionTemplate sqlSessionTemplate (@Qualifier("sqlSessionFactory") SqlSessionFactory
        sqlSessionFactory) throws Exception {
            return new SqlSessionTemplate(sqlSessionFactory);
        }




}