package com.lch.LchTool;

import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Data
public class JDKProxyCreater {

    // 实现类的接口数组

    private Class<?>[] interfaces;

    private Class<?> proxyClass;

    // 生成类的构造器

    private Constructor<?> proxyConstructor;

    // 动态代理的操作对象

    private InvocationHandler invocationHandler;

    /**
     * @param interfaces  数组 实际上只有1个 即实例化的类 实现的接口就是目标接口
     * @param invocationHandler jdk动态代理的实例化对象 实现invoke方法
     * @throws NoSuchMethodException
     */
    public JDKProxyCreater( Class<?>[] interfaces,
                           InvocationHandler invocationHandler) throws NoSuchMethodException {
        this.interfaces = interfaces;
        this.invocationHandler = invocationHandler;
        this.proxyClass = Proxy.getProxyClass(JDKProxyCreater.class.getClassLoader(), this.interfaces);
        this.proxyConstructor = this.proxyClass.getConstructor(InvocationHandler.class);
   }

    public Class<?> getProxyClass() {
        return this.proxyClass;
    }
}
