package com.control;

import com.pojo.Person;
import com.tl.FreemarkView;
import com.tl.MvcMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
public class LoginControl {

    @MvcMapping("/login.do")
    public FreemarkView login(String id){
        FreemarkView freemarkView = new FreemarkView("/login.ftl");
        System.out.println("获取的id:"+id);
        freemarkView.setMap("name","Welcome to login!");
        return freemarkView;
    }

    @MvcMapping("/list.do")
    public FreemarkView list(Integer num){
        FreemarkView freemarkView = new FreemarkView("list.ftl");
        System.out.println("获取的num:"+num);
        freemarkView.setMap("list",num+"我在我家！");
        return freemarkView;
    }

    @MvcMapping("/commit.do")
    public FreemarkView commit(Person per, String id, Integer num, Date date){
        System.out.println(per.toString());
        System.out.println(id);
        System.out.println(num);
        System.out.println(date);
        return null;
    }


}
