package com.my.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {
        "com.my.db.service.provider"
//        "com.imbidgod.asyncService",
        //imbidgo-redis-service
//        "com.imbidgo.cache.service.provider"
})

@Import({
        //imbidgod-db-service
        com.my.db.config.EntityServiceConfig.class
        //imbidgo-redis-service
//        com.imbidgod.redis.beanServiceConfig.ServiceAccessConfig.class
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
