package com.my.db.service.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

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
        entity.setAccount("Benn");
        entity.setPassword("1234");
        entity.setIsActive(1);
        entity.setName("Benny");
//        entity.setCreateBy("Admin");
//        entity.setCreateDate(Calendar.getInstance().getTime());
        //System.out.println(service.createEntity(entity));
        System.out.println(new ObjectMapper().writeValueAsString(entity));
    }

    @Test
    public void testGetAll(){
            Iterable<Member> members = service.getAllEntities();
            System.out.println(members);
            for(Member m:members){
                System.out.println(m);
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
//        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//        String json = "{\"id\":1,\"account\":\"Benn\",\"password\":\"1234\",\"isActive\":1,\"name\":\"Benny\",\"createBy\":\"System\",\"createDate\":\"2024-04-25 14:30:07\",\"updateBy\":null,\"updateDate\":null}";
//        Member m = gson.fromJson(json, Member.class);
//        System.out.println(m);
    }

    @Test
    public void testGson2(){
        String json = "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"account\": \"Benn\",\n" +
                "    \"password\": \"1234\",\n" +
                "    \"isActive\": 1,\n" +
                "    \"name\": \"Benny\",\n" +
                "    \"createBy\": \"System\",\n" +
                "    \"createDate\": \"2024-04-25 14:30:08\",\n" +
                "    \"updateBy\": null,\n" +
                "    \"updateDate\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"account\": \"benny\",\n" +
                "    \"password\": \"$2a$10$RQYs52FMxAlWnNQeeuVPiOoI5OxgYwaALvN02MiDm.6F0DiiJbxrq\",\n" +
                "    \"isActive\": 1,\n" +
                "    \"name\": \"羅智全\",\n" +
                "    \"createBy\": \"System\",\n" +
                "    \"createDate\": \"2024-04-25 15:50:33\",\n" +
                "    \"updateBy\": null,\n" +
                "    \"updateDate\": null\n" +
                "  }\n" +
                "]";
//        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//        Object members = gson.fromJson(json, List.class);
//        System.out.println(members);

    }


    @Test
    public void test3() throws JsonProcessingException {
//        String json = "[\n" +
//                "  {\n" +
//                "    \"id\": 1,\n" +
//                "    \"account\": \"Benn\",\n" +
//                "    \"password\": \"1234\",\n" +
//                "    \"isActive\": 1,\n" +
//                "    \"name\": \"Benny\",\n" +
//                "    \"createBy\": \"System\",\n" +
//                "    \"createDate\": \"2024-04-25 14:30:08\",\n" +
//                "    \"updateBy\": null,\n" +
//                "    \"updateDate\": null\n" +
//                "  },\n" +
//                "  {\n" +
//                "    \"id\": 2,\n" +
//                "    \"account\": \"benny\",\n" +
//                "    \"password\": \"$2a$10$RQYs52FMxAlWnNQeeuVPiOoI5OxgYwaALvN02MiDm.6F0DiiJbxrq\",\n" +
//                "    \"isActive\": 1,\n" +
//                "    \"name\": \"羅智全\",\n" +
//                "    \"createBy\": \"System\",\n" +
//                "    \"createDate\": \"2024-04-25 15:50:33\",\n" +
//                "    \"updateBy\": null,\n" +
//                "    \"updateDate\": null\n" +
//                "  }\n" +
//                "]";

        String json="[{\"id\":1,\"account\":\"Benn\",\"password\":\"1234\",\"isActive\":1,\"name\":\"Benny\",\"createBy\":\"System\",\"createDate\":\"2024-04-25 14:30:08\",\"updateBy\":null,\"updateDate\":null},{\"id\":2,\"account\":\"benny\",\"password\":\"$2a$10$RQYs52FMxAlWnNQeeuVPiOoI5OxgYwaALvN02MiDm.6F0DiiJbxrq\",\"isActive\":1,\"name\":\"羅智全\",\"createBy\":\"System\",\"createDate\":\"2024-04-25 15:50:33\",\"updateBy\":null,\"updateDate\":null}]";

        ObjectMapper mapper = new ObjectMapper();
        List<Member> members = mapper.readValue(json, new TypeReference<List<Member>>() {});
        // 1. convert JSON array to Array objects
//        List<Member> members = mapper.readValue(json, List.class);
        for (Member p : members) {
            System.out.println(p);
        }
    }

}
