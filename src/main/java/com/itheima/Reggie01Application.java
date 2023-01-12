package com.itheima;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j//日志
@SpringBootApplication//启动
@ServletComponentScan//扫描
@EnableTransactionManagement//开启事务功能
@EnableCaching//开启缓存注解功能
public class Reggie01Application {

    public static void main(String[] args) {
        SpringApplication.run(Reggie01Application.class, args);
        log.info("项目启动成功");
    }

}
