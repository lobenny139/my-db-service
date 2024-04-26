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

    String cacheKey = "Table.Member.";
    int cacheTime = 300; // seconds


    @Autowired(required = true)
    @Qualifier(value = "memberRepository")
    protected IMemberRepository repository;

    @Autowired(required = true)
    @Qualifier("redisService")
    protected IRedisService redisService;

    @Override
    public Member getEntityByAccount( String account )  {
        Object obj = null;
        if (getRedisService().hasKey(cacheKey + account)) {
            String json = getRedisService().get(cacheKey + account).toString();
            try {
                obj = new ObjectMapper().readValue(json, Member.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            obj = getRepository().findEntityByAccount(account);
        }
        //
        if (null != obj) {
            try {
                getRedisService().set(cacheKey + account,
                        new ObjectMapper().writeValueAsString(obj),
                        cacheTime);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return (Member) obj;
        } else {
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
            getRedisService().set(  cacheKey+member.getAccount(),
                                        new ObjectMapper().writeValueAsString(entity),
                                        cacheTime
                                );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return member;
    }

    public Iterable<Member> getAllEntities() {
        Object obj = null;
        if( getRedisService().hasKey( cacheKey+"All" ) ){
            String json = getRedisService().get(cacheKey+"All").toString();
//            obj = tools.convertJson2Object(json, List.class);
            try {
                obj = new ObjectMapper().readValue(json, new TypeReference<List<Member>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }else{
            obj = super.getAllEntities();
        }
        //
        if(null != obj  ){
            try {
                getRedisService().set(cacheKey+"All",
                        new ObjectMapper().writeValueAsString(obj),
                                        cacheTime
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return (Iterable<Member>) obj;
    }



}


