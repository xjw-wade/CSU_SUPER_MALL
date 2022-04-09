package com.csu.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//ex要JPA操作的对象
@EnableJpaRepositories(basePackages = {"com.csu.mall.persistence", "com.csu.mall.pojo"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        //SpringApplication的run方法返回一个上下文的容器实例
        ApplicationContext context = SpringApplication.run(Application.class, args);

    }
}
