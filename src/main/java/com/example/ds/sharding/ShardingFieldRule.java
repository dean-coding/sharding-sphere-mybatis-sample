package com.example.ds.sharding;

import lombok.NonNull;

/**
 * 分片字段规则
 *
 * @author fuhw/Dean
 * @date 2019-06-11
 */
public class ShardingFieldRule {

    private ShardingFieldRule() {
    }

    private static final String UNDERLINE_STRING = "_";

    /**
     * @param shardingValue 分片值
     * @param size 分片大小
     * @return 获取分片索引位
     */
    public static String getShardingIndex(@NonNull Object shardingValue, @NonNull int size) {
        return String.valueOf(Math.abs(shardingValue.hashCode()) & (size - 1));
    }

    /**
     *
     * @param shardingValue 分片值
     * @param size 分片大小
     * @return 获取分片索引（带前缀）
     */
    public static String getShardingIndexWithPrefix(@NonNull Object shardingValue, @NonNull int size) {
        return String.join(UNDERLINE_STRING, getShardingIndex(shardingValue, size));
    }
}
