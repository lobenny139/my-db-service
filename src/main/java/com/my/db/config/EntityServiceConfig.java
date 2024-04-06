package com.my.db.config;

import com.my.db.service.IMemberService;
import com.my.db.service.provider.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityServiceConfig {
    /*
    // ---------------------- 在這註冊你的資料表存取服務 -------------------
     */
    @Bean(name="memberService")
    public IMemberService memberService(){
        return new MemberService();
    }

}
