package com.my.db.service.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.my.db.access.tool.EntityService;
import com.my.db.entity.Member;
import com.my.db.exception.EntityNotFoundException;
import com.my.db.repository.IMemberRepository;
import com.my.db.service.IMemberService;
import com.my.db.util.Tools;
import com.my.redis.service.IRedisService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;


@Getter
@Setter
@Component
@Service
public class MemberService extends EntityService<Member, Long> implements IMemberService {

    String cacheKey = "Table.Member.";
    int cacheTime = 300; // seconds


    @Autowired(required = true)
    @Qualifier(value = "memberRepository")
    protected IMemberRepository repository;

    @Autowired(required = true)
    @Qualifier("redisService")
    protected IRedisService redisService;

    @Autowired(required = true)
    @Qualifier("tools")
    protected Tools tools;


    @Override
    public Member getEntityByAccount(String account){
        Object obj = null;
        if( getRedisService().hasKey( cacheKey+account ) ){
            String json = getRedisService().get(cacheKey+account).toString();
            obj = tools.convertJson2Object(json, Member.class);
        }else{
            obj = getRepository().findEntityByAccount(account);
        }
        //
        if(null != obj  ){
            getRedisService().set(cacheKey+account, obj, cacheTime);
            return (Member)obj;
        }else{
            throw new EntityNotFoundException(getChildsGenericClass().getSimpleName(), "account", account.toString());
        }
    }

    @Override
    public Member createEntity(Member entity) {
        try {
            entity.setUpdateDate(null);
            entity.setUpdateBy(null);
            if (entity.getCreateDate() == null) {
                entity.setCreateDate(new Date());
            }
            if (entity.getCreateBy() == null) {
                entity.setCreateBy("System");
            }
            Member member = super.createEntity(entity);
            getRedisService().set(cacheKey+member.getAccount(), member, cacheTime);
            return member;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}


