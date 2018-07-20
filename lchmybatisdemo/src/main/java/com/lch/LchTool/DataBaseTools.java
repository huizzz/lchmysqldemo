package com.lch.LchTool;

import com.lch.Entity.RepositoryInfo;
import com.lch.annotationtool.LchRepository;
import com.lch.autoConfig.LchProperties;
import com.lch.fuck.JdbcTool;
import com.lch.fuck.JdbcToolImlp;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * BeanFactoryPostProcessor类的作用是在被加载的bean在加载到spring容器前允许最后一次修改
 *
 * 关键的入口就是通过Reflections框架获取所有的被注解的接口 然后通过反射生成实体类 然后通过spring的bean的封装
 * 将其注入到spring容器中
 *
 * 当我们调用接口的时候 实际上是执行MyInvocationHandler类中的invoker方法
 * 即 我们只需要将主逻辑写在invoker方法里即可
 *
 * 注意:这里面的所有对象 都只能defaultListableBeanFactory容器中拿 而不能通过自动注入的方式
 * 因为该类远早于spring的加载
 *
 * 弄清BeanFactoryPostProcessor和BeanPostProcessor的区别
 * @author dly_lee
 */
@Slf4j
public class DataBaseTools implements BeanFactoryPostProcessor {

    private DefaultListableBeanFactory defaultListableBeanFactory;

    private LchProperties lchProperties;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
            throws BeansException {

        this.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;


        this.lchProperties = getLchProperties();
        // 现将 mybatis的模仿类注入容器
        registryBean(defaultListableBeanFactory);

        try {
            // 反射包获取带注解的接口
            var  requests = new Reflections(getBaseScanPackage()).getTypesAnnotatedWith(LchRepository.class);
            for (Class<?> request : requests) {
                if (request.isInterface()) {
                    createProxyClass(request);
                }
            }
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void registryBean(DefaultListableBeanFactory defaultListableBeanFactory) {
        var bean = BeanDefinitionBuilder.genericBeanDefinition(JdbcToolImlp.class).getBeanDefinition();
        defaultListableBeanFactory.registerBeanDefinition(JdbcTool.class.getSimpleName(),bean);
    }

    private void createProxyClass(Class<?> cls ) throws NoSuchMethodException {


        // 获取注解上的和配置文件的参数
        final var restInfo = extractRestInfo(cls);
        // 生成MyInvocationHandler 代理对象
        // 所有的主逻辑在这里面执行
        var handler = new MyInvocationHandler(restInfo,this.defaultListableBeanFactory);
        // 生成对应的spring可识别的bean
        var beanDefinition = createBean(cls,handler);
        // 向容器中注入我们需要的bean
        this.defaultListableBeanFactory.registerBeanDefinition(cls.getSimpleName(),beanDefinition);

        log.info("{} -> beanContext",cls.getSimpleName());
    }

    private BeanDefinition createBean(Class<?> cls, MyInvocationHandler handler) throws NoSuchMethodException {
        var proxyClass = new JDKProxyCreater(new Class<?>[]{cls},handler).getProxyClass();
        return BeanDefinitionBuilder.genericBeanDefinition(proxyClass).addConstructorArgValue(handler).getBeanDefinition();
    }

    private RepositoryInfo extractRestInfo(Class<?> cls) {
        var info = new RepositoryInfo();
        var db = cls.getAnnotation(LchRepository.class).db();
        info.setDbTable(db);
        info.setUrl(lchProperties.getUrl());
        info.setUsername(lchProperties.getUsername());
        info.setPassword(lchProperties.getPassword());
        return info;
    }

    private String getBaseScanPackage() throws NoSuchMethodException, ClassNotFoundException{

        log.info("scanner!!!!! -------------");
        String baseScanPackage = lchProperties.getPackageScanner();

        if (baseScanPackage == null){
            throw new RuntimeException("包扫描路径为空 请检查");
        }
        // 如果不是JUnit启动容器的，可以使用自动获取路径。JUnit启动的，只能使用硬编码或者配置文件
        if (!StackTraceHelper.isRunByJunit(StackTraceHelper.getMainThreadStackTraceElements())) {
            baseScanPackage = StackTraceHelper.getBasePackageByMain(2);
        }
        log.info("url -> {}",baseScanPackage);
        return baseScanPackage;
    }

    private LchProperties getLchProperties()  {
        var properties = new Properties();
        var loader = Thread.currentThread().getContextClassLoader();

        try {
            properties.load(loader.getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        var lchProperties = new LchProperties();
        lchProperties.setPackageScanner(properties.getProperty("lch.mybatis.package-scanner"));
        lchProperties.setPassword(properties.getProperty("lch.mybatis.password"));
        lchProperties.setUsername(properties.getProperty("lch.mybatis.username"));
        lchProperties.setUrl(properties.getProperty("lch.mybatis.url"));
        return lchProperties;
    }
}
