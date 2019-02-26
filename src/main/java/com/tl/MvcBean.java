package com.tl;

import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;

public class MvcBean {
    private WebApplicationContext context;
    Object target;
    Method method;
    Object[] args;

    public MvcBean(WebApplicationContext context, Object target, Method method) {
        this.context = context;
        this.target = target;
        this.method = method;
    }

    public WebApplicationContext getContext() {
        return context;
    }

    public void setContext(WebApplicationContext context) {
        this.context = context;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
