package com.my.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {
        "com.my.db.service.provider",
        "com.my.db.util",
//        "com.imbidgod.asyncService",
        //my-redis-service
        "com.my.redis.service.provider",
})

@Import({
        //imbidgod-db-service
        com.my.db.config.EntityServiceConfig.class,
        //my-redis-service
        com.my.redis.config.RedisConfig.class,
        com.my.redis.config.RedisServiceConfig.class,
        com.my.redis.config.RedisMessageConfig.class
})

@EnableJpaRepositories(basePackages = {
        //imbidgod-db-service
        "com.my.db.repository"
})

@EntityScan(basePackages = {
        // @imbidgo-db-entity
        "com.my.db.entity"
})
@SpringBootApplication
//@EnableAsync
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
