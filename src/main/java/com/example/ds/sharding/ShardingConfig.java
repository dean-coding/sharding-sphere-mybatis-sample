package com.example.ds.sharding;

import com.example.ds.CustomerHiKariConfigProperties;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.core.api.ShardingDataSourceFactory;
import io.shardingsphere.core.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.api.config.strategy.NoneShardingStrategyConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * 分片配置（数据源，表配置）
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