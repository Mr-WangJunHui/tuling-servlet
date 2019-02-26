package com.tl;

import com.control.LoginControl;
import com.pojo.Person;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.*;

import java.io.Serializable;
import java.lang.reflect.*;

import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainServlet extends HttpServlet {
    WebApplicationContext context;
    MvcBeanFactory mvcBeanFactory;
    Configuration freemarkConfiguration;

    @Override
    public void init() throws ServletException {

       /* System.out.println("init()");*/
        //初始spring容器
        context =  WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        //初始化bean工厂
        mvcBeanFactory = new MvcBeanFactory(context);
        //初始化视图模板
        freemarkConfiguration = null;
        try {
            freemarkConfiguration = context.getBean(Configuration.class);
        }catch (Exception e){
        }
        if(freemarkConfiguration == null){

            try {
                freemarkConfiguration =  new Configuration(Configuration.VERSION_2_3_23);
                freemarkConfiguration.setDefaultEncoding("UTF-8");
                freemarkConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                freemarkConfiguration.setDirectoryForTemplateLoading(new File(getServletContext().getRealPath("/ftl")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.freemarkConfiguration = freemarkConfiguration;

        }


    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*String uri = req.getRequestURI();
        String path = req.getServletPath();
        StringBuffer url =req.getRequestURL();
        req.getRemoteAddr();
        req.getLocalAddr();
        System.out.println("uri==="+uri);
        System.out.println("path==="+path);
        System.out.println("url==="+url);*/
        //处理业务请求
        doHandle(req,resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     /* System.out.println("doPost()");*/
        //处理业务请求
        doHandle(req,resp);
    }

    private void doHandle(HttpServletRequest req,HttpServletResponse resp){
        try {
            //获取请求的url
            String url = req.getServletPath();
            MvcBean mvcBean = (MvcBean) mvcBeanFactory.getMvcBeanByUrl(url);
            Object[] args = null;
            if(mvcBean != null){
                //获取方法里面的参数并赋值
                args = bulidParams(mvcBean,req,resp);
            }


            //执行拦截方法
           //Object modelAndView =  mvcBean.getMethod().invoke(mvcBean.getTarget(),args);
            Object modelAndView =  mvcBean.getMethod().invoke(mvcBean.getTarget(),args);
           if( modelAndView instanceof FreemarkView){
               FreemarkView freemarkView = (FreemarkView)modelAndView;
               Template template =  freemarkConfiguration.getTemplate(((FreemarkView) modelAndView).getPath());
               resp.setCharacterEncoding("utf-8");
               resp.setContentType("text/html; charset=utf-8");
               resp.setStatus(200);
               template.process(freemarkView.getMap(),resp.getWriter());
           }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (TemplateException e){
            e.printStackTrace();
        }

    }

    public Object[] bulidParams(MvcBean mvcBean,HttpServletRequest req,HttpServletResponse resp){
        //执行的方法
        Method method = mvcBean.getMethod();
        //执行的对象
        Object obj = mvcBean.getTarget();
        LocalVariableTableParameterNameDiscoverer lvtpn = new LocalVariableTableParameterNameDiscoverer();
        String[] parames = lvtpn.getParameterNames(method);

        Class[] types =  method.getParameterTypes();

        Object[] args = new Object[parames.length];

        for(int i=0;i<parames.length;i++){
            //获取参数类型
            Class type = types[i];
            //获取形参的名字
            String paramName = parames[i];

            String k = type.getTypeName();
            if(type.isAssignableFrom(HttpServletRequest.class)){
                args[i] = req;
            }else if(type.isAssignableFrom(HttpServletResponse.class)){
                args[i] = resp;
            }else{
                //其他类型转换
                try {
                    args[i] = convert(req,type,paramName);
                } catch (ParseException e) {
                    throw new RuntimeException("类型转换异常！");
                }
            }

        }



        //构建参数赋值

        //返回参数的对象数组
        return  args;
    }

    public Object convert(HttpServletRequest req,Class type,String paramName) throws ParseException {
        //八大基本类型+时间+日期
        Object arg =  parse(req.getParameter(paramName),type);

        if(arg != null){
            return arg;
        }
        //对象类型的转换
        if(Serializable.class.isAssignableFrom(type) && !String.class.isAssignableFrom(type)){
            //创建参数类型的对象
            Object paramobj = null;
            try {
                paramobj = Class.forName(type.getName()).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            //获取到对象的类型
           for(Field filed:type.getDeclaredFields()) {
               String  filedValue = req.getParameter(paramName+"."+filed.getName());

               if(!StringUtils.isEmpty(filedValue)){
                   try {

                       Method method = type.getMethod("set"+firstUpper(filed.getName()),filed.getType());
                       method.invoke(paramobj,parse(filedValue,filed.getType()));
                   } catch (NoSuchMethodException e) {
                       e.printStackTrace();
                   } catch (IllegalAccessException e) {
                       e.printStackTrace();
                   } catch (InvocationTargetException e) {
                       e.printStackTrace();
                   }
               }
               arg = paramobj;
            }
           return arg;
        }else if(StringUtils.isEmpty(req.getParameter(paramName))){
           return  null;
        } else{
            throw new RuntimeException("未找到指定的参数！");
        }
    }



    public String firstUpper(String field){
       if(StringUtils.isEmpty(field)){
           throw new RuntimeException("param not empty");
       }
       String first = field.substring(0,1);
       return first.toUpperCase()+field.substring(1);
    }

    public Object parse(String inParam,Class paramClass) throws ParseException {
        Object arg = new Object();
        if(paramClass.isAssignableFrom(Integer.class)){
            return Integer.valueOf(inParam.trim());
        }else if(paramClass.isAssignableFrom(Double.class)){
            return Double.valueOf(inParam.trim());
        } else if(paramClass.isAssignableFrom(Float.class)){
            return Float.valueOf(inParam.trim());
        } else if(paramClass.isAssignableFrom(Character.class)){
            char[] chars =  inParam.trim().toCharArray();
            return (Character)chars[0];
        } else if(paramClass.isAssignableFrom(Short.class)){
            return Short.valueOf(inParam.trim());
        }else if(paramClass.isAssignableFrom(Boolean.class)){
            return Boolean.valueOf(inParam.trim());
        }else if(paramClass.isAssignableFrom(Byte.class)){
            return Byte.valueOf(inParam.trim());
        }else if(paramClass.isAssignableFrom(Long.class)){
            return Long.valueOf(inParam.trim());
        }else if(paramClass.isAssignableFrom(Date.class)){
            return new SimpleDateFormat("YYYY-MM-DD").parse(inParam.trim());
        } else if(paramClass.isAssignableFrom(String.class)) {
            return inParam;
        }
        return null;
    }
}
