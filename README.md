
## sharding-sphere-mybatis-sample


## 核心概念

### Sharding Key
分片中使用的数据库字段是指数据库（表）的水平分片中的关键字段。例如，在订单ID分片的最后模数中，订单ID被用作分片密钥。
SQL中没有分片字段时执行的完整路由性能较差。除了单个分片列之外，ShardingSphere还支持多个分片列。

### Sharding Algorithm

数据的分片可以通过分片算法来实现=，BETWEEN和IN。高度灵活的分片算法需要由开发人员自己实现。
当前，有4种分片算法可用。由于分片算法和业务成就高度相关，因此它不提供内置的分片算法，而是通过分片策略
提取各种情况以提供更高的抽象度，并为开发人员自己实现分片算法提供接口。

```asp

精确分片算法
PreciseShardingAlgorithm用于处理使用单个分片密钥=和的分片情况IN，以及StandardShardingStrategy。

范围分片算法
RangeShardingAlgorithm用于处理BETWEEN AND使用单个分片密钥的分片情况，以及StandardShardingStrategy。

复杂键分片算法
ComplexKeysShardingAlgorithm用于处理其中使用多个分片密钥的分片情况ComplexShardingStrategy。它具有相对复杂的逻辑，
需要开发人员自行处理。

提示分片算法
HintShardingAlgorithm将处理与Hint一起使用的分片情况HintShardingStrategy。

```

### Sharding Strategy

它包括Sharding Key和Sharding algorithm，后者由于其独立性而被提取出来。在分片操作中只能使用分片键+分片算法，即分片策略。
目前，有5种分片策略可用。

```asp

标准分片策略
StandardShardingStrategy提供支持的分片运行=，IN并BETWEEN AND在SQL。 StandardShardingStrategy仅支持单个分片密钥，
并提供两个分片算法PreciseShardingAlgorithm和RangeShardingAlgorithm。 PreciseShardingAlgorithm是强制性的，并用
于操作的分片=和IN。 RangeShardingAlgorithm是可选的，用于操作的分片BETWEEN AND。 BETWEEN AND在SQL中，无需配置即可
通过所有数据节点路由进行操作RangeShardingAlgorithm。

复杂分片策略
ComplexShardingStrategy提供支持的分片运行=，IN并BETWEEN AND在SQL。 ComplexShardingStrategy虽然支持多个分片密钥，
但是由于它们之间的关系是如此复杂，以至于没有太多封装，因此分片密钥和分片运算符的组合位于算法接口中，并且由开发人员以最
大的灵活性实现。

内联分片策略
使用Groovy表达式，InlineShardingStrategy可以为SQL =和INSQL中的分片操作提供单键支持。可以通过简单的配置使用简单的分
片算法，以避免费力的Java代码开发。例如，将t_user_$->{u_id % 8}表t_user根据u_id分为8个表，表名从t_user_0
到 t_user_7。

提示分片策略
HintShardingStrategy 是指通过Hint而不是SQL解析进行分片​​的策略。

无分片策略
NoneShardingStrategy 指没有分片的策略。

```


## Get started

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
  
  
  