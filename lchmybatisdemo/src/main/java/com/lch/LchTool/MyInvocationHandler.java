package com.lch.LchTool;

import com.lch.Entity.RepositoryInfo;
import com.lch.annotationtool.LchDelete;
import com.lch.annotationtool.LchInsert;
import com.lch.annotationtool.LchQuery;
import com.lch.annotationtool.LchUpdate;
import com.lch.fuck.JdbcTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 执行接口的时候 调用的主逻辑类
 * @author dly_lee
 */
@Slf4j
public class MyInvocationHandler implements InvocationHandler {

    private RepositoryInfo info;

    private DefaultListableBeanFactory beanFactory;

    private JdbcTool jdbcTool;

    public MyInvocationHandler(RepositoryInfo info, DefaultListableBeanFactory beanFactory) {
        this.info = info;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Exception {

        var jdbcTool = getJDBCTool();

        var returnType = method.getGenericReturnType();

        // 查
        if (method.isAnnotationPresent(LchQuery.class)){
            var querySql = method.getAnnotation(LchQuery.class).value();
            return jdbcTool.find(info,querySql,args,returnType);
        // 改
        }else if (method.isAnnotationPresent(LchUpdate.class)){
            var querySql = method.getAnnotation(LchUpdate.class).value();
            return jdbcTool.insertAndUpdate(info,querySql,args,returnType);
        }
        // 增
        else if (method.isAnnotationPresent(LchInsert.class)){
            var querySql = method.getAnnotation(LchInsert.class).value();
            return jdbcTool.insertAndUpdate(info,querySql,args,returnType);
        // 删
        } else if (method.isAnnotationPresent(LchDelete.class)){
            var querySql = method.getAnnotation(LchDelete.class).value();
            return jdbcTool.insertAndUpdate(info,querySql,args,returnType);
        }else {
            throw new RuntimeException("方法上没有发现注解 请检查");
        }
    }

    private JdbcTool getJDBCTool(){

        if (jdbcTool == null){
            this.jdbcTool = this.beanFactory.getBean(JdbcTool.class);
        }
        return jdbcTool;
    }
}
