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
import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
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
import java.util.*;
import java.util.stream.Collectors;

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
     * ?????????Druid??????
     * @param
     * @return {@code org.springframework.boot.web.servlet.ServletRegistrationBean}
     * @author ykf
     * @date 2021/4/12 10:51
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        //??????servlet????????????
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //??????ip?????????
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        //???????????????????????????
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        //????????????????????????
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }


    /**
     *
     * ????????????????????????????????????????????????????????????ioc ??????
     * @param dsProp
     * @return {@code javax.sql.DataSource}
     * @author ykf
     * @date 2021/4/12 10:49
     */
    public DataSource ds0(@NotNull DsProps.DsProp dsProp) {
        Map<String, Object> dsMap = new HashMap<>();
        ValidationUtil.assertNotNull(dsProp.getType(),"???????????????????????????");
        ValidationUtil.assertNotNull(dsProp.getUrl(),"??????????????????urL");
        ValidationUtil.assertNotNull(dsProp.getUsername(),"??????????????????");
        ValidationUtil.assertNotNull(dsProp.getPassword(),"??????????????????");
        dsMap.put("type",  dsProp.getType());
        dsMap.put("url", dsProp.getUrl());
        dsMap.put("username", dsProp.getUsername());
        dsMap.put("password", dsProp.getPassword());
        List<DsProps.DsProp> slaveDs = dsProp.getSlaveDs();
        if (slaveDs!=null){

        }

        DruidDataSource ds = (DruidDataSource) DataSourceUtil.buildDataSource(dsMap);
        ds.setProxyFilters(Lists.newArrayList(statFilter()));
        // ??????????????????????????????
        ds.setMaxActive(20);
        // ??????????????????????????????
        ds.setMinIdle(5);
        return ds;
    }



    /**
     *
     * ??????sharding ????????????
     * @param dsProps
     * @return {@code javax.sql.DataSource}
     * @author ykf
     * @date 2021/4/12 10:44
     */
    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DsProps dsProps) throws SQLException {
        //??????
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        List<DsProps.DsProp> ds = dsProps.getDs();
        ValidationUtil.assertNotNull(ds, "?????????????????????");

        // ?????????????????????
        Map<String, DataSource> dataSourceMap = new HashMap<>();

      List<DsProps.DsProp> allDs =  ds.stream().flatMap(t1->{
            List<DsProps.DsProp> dcs = new ArrayList<>();
            dcs.add(t1);
            List<DsProps.DsProp> slaveDs = t1.getSlaveDs();
            if (slaveDs!=null&&slaveDs.size()>0){
                dcs.addAll(slaveDs);
            }
            return dcs.stream();
        }).collect(Collectors.toList());
        for (DsProps.DsProp dsProp : allDs) {
            log.info("[???????????????]{}", dsProp.getDcName());
            dataSourceMap.put(dsProp.getDcName(), ds0(dsProp));
        }

        /*List<TableRuleConfigurationEnum> tableRuleConfigurationEnums = TableRuleConfigurationEnum
                .TableRuleConfigurationEnum(dsProp.getDcName());*/
       List<TableRuleConfigurationEnum> tableRuleConfigurationEnums = Arrays.asList(TableRuleConfigurationEnum.values());
        if (tableRuleConfigurationEnums !=null&&tableRuleConfigurationEnums.size()>0){
            tableRuleConfigurationEnums.forEach(
                    dbNameEnums->{
                        shardingRuleConfig.getTableRuleConfigs().add(ruleConfig(dbNameEnums));
                    }
            );
        }
        //  ????????????
        shardingRuleConfig.setMasterSlaveRuleConfigs(getMasterSlaveRuleConfigs(ds));
        // ??????????????????
        List<String> tableGroups = BindingTableGroupsEnum.getTableByAdminType("ADMIN_DB");
        if (tableGroups != null) {
            for (String bindingTable : tableGroups) {
                shardingRuleConfig.getBindingTableGroups().add(bindingTable);
            }
        }
        // ?????????????????????
        Properties p = new Properties();
        p.setProperty("sql.show", Boolean.TRUE.toString());

       DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig,p);
            return dataSource;
        }

        //  ??????????????????
    private Collection<MasterSlaveRuleConfiguration>  getMasterSlaveRuleConfigs(List<DsProps.DsProp> dsProps) {
        if (dsProps !=null){
            return dsProps.stream().map(dsProp -> {
                List<DsProps.DsProp> slaveDs = dsProp.getSlaveDs();
                if (slaveDs != null) {
                    List<String> slaveDataSourceNames = slaveDs.stream().map(DsProps.DsProp::getDcName).collect(Collectors.toList());
                  //  String name = dsProp.getDcName()+"_"+slaveDataSourceNames.stream().collect(Collectors.joining());
                   return new MasterSlaveRuleConfiguration("ms0", dsProp.getDcName(),slaveDataSourceNames
                            , new LoadBalanceStrategyConfiguration("ROUND_ROBIN"));
                }
                return null;
            }).filter(t1 -> t1 != null).collect(Collectors.toList());
        }
        return null;
    }


    /**
     *
     * ???????????????
     * @param tableRuleConfigurationEnum
     * @return {@code io.shardingsphere.api.config.rule.TableRuleConfiguration} ??????????????????
     * @author ykf
     * @date 2021/4/12 10:54
     */

    private TableRuleConfiguration ruleConfig(TableRuleConfigurationEnum tableRuleConfigurationEnum) {
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getLogicTable(),"?????????????????????");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getDbName(),"???????????????????????????");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getExpression(),"?????????????????????");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getGeneratorColumnName(),"??????????????????");
        ValidationUtil.assertNotNull(tableRuleConfigurationEnum.getShardingColumn(),"?????????????????????");

        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration(tableRuleConfigurationEnum.getLogicTable(),"ms0"+
                "."+tableRuleConfigurationEnum.getLogicTable()+tableRuleConfigurationEnum.getExpression());
        tableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE",tableRuleConfigurationEnum.getGeneratorColumnName()) );

        tableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration
                (tableRuleConfigurationEnum.getShardingColumn(),
                new CreateFieldShardingAlgorithm()));
        return tableRuleConfig;
    }

        /**
         *
         * ?????????????????????????????????
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
         * mybatis ????????????sqlSessionFactory
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