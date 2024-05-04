package com.my.db.service.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;


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

    @Autowired
    @Qualifier(value = "passwordEncoder")
    protected PasswordEncoder bcryptEncoder;

    //    https://stackoverflow.com/questions/48488735/how-can-i-get-session-from-a-common-class-in-spring-boot
    @Autowired(required = false)
    private ObjectFactory<HttpSession> httpSessionFactory;

    protected String getCurrentUser(){
        if(getHttpSessionFactory().getObject() != null && getHttpSessionFactory().getObject().getAttribute("currentUser") != null){
            return getHttpSessionFactory().getObject().getAttribute("currentUser").toString();
        }else{
            return "Test Case";
        }
    }


    /**
     * json 轉 Objects(Iterable)
     * @param json
     * @return
     */
//    protected Iterable<Member> json2Objects(String json){
//        try {
//            return new ObjectMapper().readValue(json, new TypeReference<Iterable<Member>>() {});
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Json 無法解析, " + e);
//        }
//    }

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
            throw new EntityNotFoundException(getRuntimecClass().getSimpleName(), "account", account.toString());
        }
    }


    @Override
    public Member createEntity(Member entity) {
        entity.setUpdateDate(null);
        entity.setUpdateBy(null);
        if (entity.getCreateDate() == null) {
            entity.setCreateDate(new Date());
        }

        entity.setCreateBy(getCurrentUser());

        entity.setPassword(getBcryptEncoder().encode(entity.getPassword()));

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

        // updateDate
        dbEntity.setUpdateDate(new Date());

        //updateBy
        dbEntity.setUpdateBy(getCurrentUser());

        //password
        if(!StringUtils.isBlank(entity.getPassword())){
            dbEntity.setPassword(bcryptEncoder.encode(entity.getPassword()));
        }


        //name
        if(!StringUtils.isBlank(entity.getName())){
            dbEntity.setName(entity.getName());
        }

        // isActive
        if(entity.getIsActive() != null){
            dbEntity.setIsActive(entity.getIsActive());
        }

        Member obj =  super.saveEntity(dbEntity);

        if(getRedisService().hasKey(cacheDB, cacheKey+account)){
            getRedisService().del(cacheDB, cacheKey+account);
        }

        return obj;
    }

    @Override
    public void deleteEntityByAccount(String account) {
        getRepository().removeEntityByAccount(account);
        if(getRedisService().hasKey(cacheDB, cacheKey+account)){
            getRedisService().del(cacheDB, cacheKey+account);
        }
    }

    public void updateEntityStatusByAccount(String account, int isActivate){
        if(getRedisService().hasKey(cacheDB, cacheKey+account)){
            getRedisService().del(cacheDB, cacheKey+account);
        }

        getRepository().activateEntityByAccount(account, isActivate, getCurrentUser());
    }


}


