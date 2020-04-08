package com.example.ds.sharding;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 分片属性配置
 * @author fuhw/Dean
 * @date 2019-06-11
 */
@Data
@Component
public class ShardingProperties {
    @Value("${sharding.prop.sqlShow:false}")
    private boolean sqlShow = false;
}