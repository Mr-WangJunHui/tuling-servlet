package com.control;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

public class TestMethod {
    public void  method1(String name,String email){
        System.out.println(name+":"+email);

    }
    @Test
    public void test(){
        Class<TestMethod> clazz = TestMethod.class;
        try {
            //得到方法实体
            Method method = clazz.getMethod("method1", String.class, String.class);
            //得到该方法参数信息数组
            Parameter[] parameters = method.getParameters();
            //遍历参数数组，依次输出参数名和参数类型

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void springGetParamName() throws NoSuchMethodException {
        LocalVariableTableParameterNameDiscoverer lvtpnd =new LocalVariableTableParameterNameDiscoverer();
        Method methods = new TestMethod().getClass().getMethod("method1",new Class[]{String.class,String.class});

        for(String param:lvtpnd.getParameterNames(methods)){
            System.out.println(param);
        }


    }
}
