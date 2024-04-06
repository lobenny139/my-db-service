package com.my.db.service.provider;

import com.my.db.access.tool.EntityService;
import com.my.db.entity.Member;
import com.my.db.exception.EntityNotFoundException;
import com.my.db.repository.IMemberRepository;
import com.my.db.service.IMemberService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Component
@Service
public class MemberService extends EntityService<Member, Long> implements IMemberService {

    @Autowired(required = true)
    @Qualifier(value = "memberRepository")
    protected IMemberRepository repository;


    @Override
    public Member getEntityByAccount(String account){
        Object obj = getRepository().findEntityByAccount(account);
        if(null != obj){
            return (Member)obj;
        }else{
            throw new EntityNotFoundException(getChildsGenericClass().getSimpleName(), "account", account.toString());
        }
    }}
