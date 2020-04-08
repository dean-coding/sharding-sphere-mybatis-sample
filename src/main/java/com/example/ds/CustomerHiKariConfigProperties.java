package com.example.ds;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义hikari数据源配置属性
 *
 * @author fuhw/DeanKano
 * @date 2019-03-28 18:35
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class CustomerHiKariConfigProperties  extends HikariConfig {
}
