package com.csu.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching // 开启缓存，不开启数据将无法存入redis
//启动es 为es和jpa分别指定不同的包名，否则会出错
@EnableElasticsearchRepositories(basePackages = "com.csu.mall.es")
//eS要JPA操作的对象
@EnableJpaRepositories(basePackages = {"com.csu.mall.persistence", "com.csu.mall.pojo"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        //SpringApplication的run方法返回一个上下文的容器实例
        ApplicationContext context = SpringApplication.run(Application.class, args);

    }
}
