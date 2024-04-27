package com.my.db.service.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.db.access.tool.EntityService;
import com.my.db.entity.Member;
import com.my.db.exception.EntityNotFoundException;
import com.my.db.repository.IMemberRepository;
import com.my.db.service.IMemberService;
import com.my.redis.service.IRedisService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@Component
@Service
public class MemberService extends EntityService<Member, Long> implements IMemberService {

    static final String cacheKey = "Table.Member.";
    static final int cacheTime = 300; // seconds
    static final int cacheDB = 9; // redis db index

    @Autowired(required = true)
    @Qualifier(value = "memberRepository")
    protected IMemberRepository repository;

    @Autowired(required = true)
    @Qualifier("redisService")
    protected IRedisService redisService;

    protected Object json2Object(String json){
        try {
            return new ObjectMapper().readValue(json, Member.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 無法解析, " + e);
        }
    }

    protected Object json2Objects(String json){
        try {
            return new ObjectMapper().readValue(json, new TypeReference<List<Member>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 無法解析, " + e);
        }
    }

    @Override
    public Member getEntityByAccount( String account )  {
        Object obj = null;
        boolean findByCache = getRedisService().hasKey(cacheDB, cacheKey + account);
        if ( findByCache ) {
            String json = getRedisService().get(cacheDB, cacheKey + account).toString();
            obj = json2Object(json);
        } else {
            obj = getRepository().findEntityByAccount(account);
        }
        //
        if (null != obj && !findByCache) {
            try {
                getRedisService().set( cacheDB,
                                    cacheKey + account,
                                        new ObjectMapper().writeValueAsString(obj),
                                        cacheTime);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Json無法存入快取, " + e);
            }
        }
        if(null != obj){
            return (Member) obj;
        }else{
            throw new EntityNotFoundException(getChildsGenericClass().getSimpleName(), "account", account.toString());
        }
    }


    @Override
    public Member createEntity(Member entity) {
        entity.setUpdateDate(null);
        entity.setUpdateBy(null);
        if (entity.getCreateDate() == null) {
            entity.setCreateDate(new Date());
        }
        if (entity.getCreateBy() == null) {
            entity.setCreateBy("System");
        }
        Member member = super.createEntity(entity);
        try {
            getRedisService().set(      cacheDB,
                                    cacheKey+member.getAccount(),
                                        new ObjectMapper().writeValueAsString(entity),
                                        cacheTime
                                );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json無法存入快取, " + e);
        }
        return member;
    }

    public Iterable<Member> getAllEntities() {
        Object obj = null;
        boolean findByCache = getRedisService().hasKey(cacheDB, cacheKey + "All");

        if( findByCache  ){
            String json = getRedisService().get(cacheDB, cacheKey+"All").toString();
            obj = json2Objects(json);
        }else{
            obj = super.getAllEntities();
        }
        //
        if(null != obj && !findByCache ){
            try {
                getRedisService().set(  cacheDB,
                                    cacheKey+"All",
                                        new ObjectMapper().writeValueAsString(obj),
                                        cacheTime
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Json無法存入快取, " + e);            }
        }
        return (Iterable<Member>) obj;
    }


    @Override
    public Member updateEntityByAccount(String account, Member entity) {
        Member dbEntity = this.getEntityByAccount(account); // from db
        dbEntity.setUpdateDate(new Date());

        if(!StringUtils.isBlank(entity.getUpdateBy())){
            dbEntity.setUpdateBy(entity.getUpdateBy());
        }
        return null;
    }



}


