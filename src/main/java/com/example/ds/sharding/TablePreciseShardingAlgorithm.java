package com.example.ds.sharding;

import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

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