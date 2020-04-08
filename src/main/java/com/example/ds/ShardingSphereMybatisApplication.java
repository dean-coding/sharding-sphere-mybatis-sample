package com.example.ds;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@MapperScan("com.example.ds.mapper")
@SpringBootApplication
public class ShardingSphereMybatisApplication {


    public static void main(String[] args) {
        SpringApplication.run(ShardingSphereMybatisApplication.class, args);
    }

}
