package com.my.db.service.test;

import com.google.gson.*;
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

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.my.db.TestApplication.class)
@TestPropertySource(locations = "/test-application.properties")
@AutoConfigureTestDatabase(replace = NONE)
@Rollback(value = false)
public class TestMemberService {

    @Autowired(required = true)
//    @Qualifier("memberService")
    private IMemberService service;



    @Test
    public void testCreate() {
        Member entity = new Member();
        entity.setAccount("Benn");
        entity.setPassword("1234");
        entity.setIsActive(1);
        entity.setName("Benny");
//        entity.setCreateBy("Admin");
//        entity.setCreateDate(Calendar.getInstance().getTime());
        System.out.println(service.createEntity(entity));
    }

    @Test
    public void testGetAll(){
        List<Member> members = (List<Member>) service.getAllEntities();
        for(Member member : members){
            System.out.println(member);
        }
    }

    @Test
    public void testGetByAccount(){
        Member member = service.getEntityByAccount("Benn");
        System.out.println(member);
    }

    @Test
    public void testGetById(){
        Member member = service.getEntityById(1L);
        System.out.println(member);
    }

    @Test
    public void testGson(){

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-ddHH:mm:ss").create();


        String json = "{\"id\":1,\"account\":\"Benn\",\"password\":\"1234\",\"isActive\":1,\"name\":\"Benny\",\"createBy\":\"System\",\"createDate\":\"2024-04-25 14:30:07\",\"updateBy\":null,\"updateDate\":null}";
        Member m = gson.fromJson(json.replaceAll(" " ,""), Member.class);
        System.out.println(m);
    }

}
