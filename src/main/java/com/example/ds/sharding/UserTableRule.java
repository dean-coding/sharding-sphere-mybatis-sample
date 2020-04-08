package com.example.ds.sharding;

import io.shardingsphere.core.api.config.TableRuleConfiguration;
import io.shardingsphere.core.api.config.strategy.StandardShardingStrategyConfiguration;

import java.util.Arrays;

/**
 * 表规则配置
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
