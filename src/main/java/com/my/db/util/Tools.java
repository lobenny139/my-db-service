package com.my.db.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.my.db.entity.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Tools {
    public Object convertJson2Object(String json, Class clazz){
        Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmss").create();
        json =  json.replaceAll(" ", "").replaceAll(":", "").replaceAll("-","");
        return gson.fromJson(json, clazz);
    }
}
