package com.tl;

import org.springframework.web.context.WebApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MvcBeanFactory {
    //上下文
    private WebApplicationContext context;

    //url的列表
    private Map<String,Object> urlMap = new HashMap<String,Object>();

    public Map<String, Object> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(Map<String, Object> urlMap) {
        this.urlMap = urlMap;
    }

    public MvcBeanFactory(WebApplicationContext context) {
        this.context = context;
        loadUrlMap();
    }

    public WebApplicationContext getContext() {
        return context;
    }

    public void setContext(WebApplicationContext context) {
        this.context = context;
    }

    //初始化url的列表
    public void loadUrlMap(){
        //得到ioc容器中所有的bean
        String[] beansName = context.getBeanDefinitionNames();
        Object bean;
        for(String beanName:beansName){
            bean =  context.getBean(beanName);
           //获取bean所有的方法
            for(Method method:bean.getClass().getMethods()){
               Annotation annotation =  method.getAnnotation(MvcMapping.class);
               if(annotation != null){
                   String url = ((MvcMapping) annotation).value();
                   MvcBean mvcBean = new MvcBean(context,bean,method);
                   urlMap.put(url,mvcBean);
               }
            }
        }
    }

    //从urlMap中获取对象mvcMapping
    public Object getMvcBeanByUrl(String url){
        if(urlMap.containsKey(url)){
            return urlMap.get(url);
        }
        return null;
    }


}
