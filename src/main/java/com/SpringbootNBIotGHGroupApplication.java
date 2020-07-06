package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.weeg.dao")
@SpringBootApplication
public class SpringbootNBIotGHGroupApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootNBIotGHGroupApplication.class, args);
    }

}
