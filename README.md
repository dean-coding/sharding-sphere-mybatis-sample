
## sharding-sphere-mybatis-sample

#### Get started


### 示例： 

### 1 maven 依赖


    <sharding-sphere.version>3.0.0.M3</sharding-sphere.version>

```asp

<dependency>
    <groupId>io.shardingsphere</groupId>
    <artifactId>sharding-jdbc</artifactId>
    <version>${sharding-sphere.version}</version>
</dependency>
<dependency>
    <groupId>io.shardingsphere</groupId>
    <artifactId>sharding-core</artifactId>
    <version>${sharding-sphere.version}</version>
</dependency>

```

### 2 核心配置



```asp

/**
 * 表规则配置
 * t_user 分表4个 t_user_0,t_user_1,t_user_2,t_user_3
 * 分片字段：user_name
 *
 * @author fuhw/Dean
 * @date 2019-06-11
 */
public final class UserTableRule {

    private UserTableRule() {
    }

    public static final String SHARDING_FIELD = "user_name";
    public static final String SHARDING_TABLE = "t_user";
    /**
     * 多个，逗号分隔
     */
    public static final String SHARDING_TABLE_GROUP = "t_user";

    /**
     * dataNode的个数（实际表）
     */
    private static int dataNodeSize = 4;
    /**
     * dataNode后缀字符串列表：[0,1...]
     */
    private static String dataNodeSuffixNumber;

    static {
        int[] tableSuffixCode = new int[dataNodeSize];
        for (int i = 0; i < dataNodeSize; i++) {
            tableSuffixCode[i] = i;
        }
        dataNodeSuffixNumber = Arrays.toString(tableSuffixCode);
    }

    /**
     * @param datasourceKey 数据源KEY
     * @return 获取表规则配置
     */
    public static TableRuleConfiguration getTableRuleConfig(String datasourceKey) {
        TableRuleConfiguration result = new TableRuleConfiguration();
        result.setLogicTable(SHARDING_TABLE);
        /**
         *  示例： default.t_user_${[0,1,2,3]}
         */
        result.setActualDataNodes(String.join("", datasourceKey, ".", SHARDING_TABLE, "_${", dataNodeSuffixNumber, "}"));
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(SHARDING_FIELD,
                new TablePreciseShardingAlgorithm()));
        return result;
    }


}

```


```asp

/**
 * 精确值的表分片算法
 */
@Slf4j
public final class TablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(final Collection<String> availableTableNames, final PreciseShardingValue<String> shardingValue) {
        if (CollectionUtils.isEmpty(availableTableNames)) {
            throw new UnsupportedOperationException(" no available tables can found ");
        }
        int size = availableTableNames.size();
        for (String each : availableTableNames) {
            if (each.endsWith(ShardingFieldRule.getShardingIndexWithPrefix(shardingValue.getValue(), size))) {
                return each;
            }
        }
        throw new UnsupportedOperationException(" no matched to the available tables ");
    }
}

```


```asp

/**
 * 分片配置（数据源，规则配置等）
 *
 * @author fuhw/Dean
 * @date 2019-06-11
 */
@Slf4j
@Configuration
public class ShardingConfig {


    private static String datasourceKey = "default";

    @Primary
    @Bean
    public DataSource getDataSource(DataSourceProperties dataProp,
                                    ShardingProperties shardingProperties,
                                    CustomerHiKariConfigProperties hiKariConfigProperties) throws SQLException {

        log.info("=================加载数据源配置================");
        log.info("url: [{}]", dataProp.getUrl());
        log.info("type：[{}]", dataProp.getType());
        log.info("driverClassName: [{}]", dataProp.getDriverClassName());
        log.info("=============================================");

        // 添加数据源（默认的配置项：spring.datasource，如需要多库再做变更）
        hiKariConfigProperties.setDriverClassName(dataProp.getDriverClassName());
        hiKariConfigProperties.setJdbcUrl(dataProp.getUrl());
        hiKariConfigProperties.setUsername(dataProp.getUsername());
        hiKariConfigProperties.setPassword(dataProp.getPassword());
        DataSource defaultDataSource = new HikariDataSource(hiKariConfigProperties);

        // 分片规则配置
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        // 配置表规则
        shardingRuleConfig.getTableRuleConfigs().add(UserTableRule.getTableRuleConfig(datasourceKey));
        shardingRuleConfig.getBindingTableGroups().add(UserTableRule.SHARDING_TABLE_GROUP);
        // 默认表规则：不分片
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());
        // 数据源配置
        Map<String, DataSource> dataSourceMap = Collections.singletonMap(datasourceKey, defaultDataSource);
        Properties properties = new Properties();
        properties.setProperty("sql.show", String.valueOf(shardingProperties.isSqlShow()));
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, Maps.newHashMap(), properties);
    }


}

```




### 注意：

```
  一 3.0.0.M2的异常
  io.shardingsphere.core.transaction.listener.TransactionListene...
  
  https://github.com/apache/incubator-shardingsphere/issues/1116
  
  升级到3.0.0.M3

```
  
  
  