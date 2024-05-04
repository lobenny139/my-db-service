package com.my.db.service.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.my.db.entity.Member;
import com.my.db.service.IMemberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.my.db.TestApplication.class)
@TestPropertySource(locations = "/test-application.properties")
@AutoConfigureTestDatabase(replace = NONE)
@Rollback(value = false)
public class TestMemberService {

    //https://mkyong.com/java/jackson-convert-json-array-string-to-list/


    @Autowired(required = true)
//    @Qualifier("memberService")
    private IMemberService service;



    @Test
    public void testCreate() throws JsonProcessingException {
        Member entity = new Member();
        entity.setAccount("benny");
        entity.setPassword("1234");
        entity.setIsActive(1);
        entity.setName("Benny Lo");
        System.out.println(service.createEntity(entity));
        //System.out.println(new ObjectMapper().writeValueAsString(entity));
    }

    @Test
    public void testGetAll(){
            Iterable<Member> members = service.getAllEntities();

            for (Member m : members) {
                System.out.println(m);
            }
    }

    @Test
    public void testGetByAccount(){
        Member member = service.getEntityByAccount("benny");
        System.out.println(member);
    }

    @Test
    public void testGetById(){
        Member member = service.getEntityById(1L);
        System.out.println(member);
    }

    @Test
    public void testUpdate(){
        String account = "benny";
        Member member = new Member();
        member.setName("方方");
        System.out.println(service.updateEntityByAccount(account, member));
    }

    @Test
    public void testDel(){
        String account = "benny";
        service.deleteEntityByAccount(account);
    }

    @Test
    public void testAdjustEntityStatusByAccount(){
        String account = "benny4";
        service.updateEntityStatusByAccount(account, 0);
    }




}
