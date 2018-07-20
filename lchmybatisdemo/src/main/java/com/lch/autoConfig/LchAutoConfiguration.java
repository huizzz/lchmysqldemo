package com.lch.autoConfig;

import com.lch.LchTool.DataBaseTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * DataBaseTools 是一个静态的修饰
 * 是为了让BeanFactoryPostProcessor 在所有的bean加载前先加载
 * 导致了一个结果就是期望 LchProperties的配置加载在这个会后加载导致无法用@Autowired
 * 解决办法就是在DataBaseTools中添加Properties读取去先去表中读取需要的数据
 * @author dly_lee
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(LchProperties.class)
@Slf4j
public class LchAutoConfiguration {



    @Bean
    static DataBaseTools dataBaseTools(){
        return new DataBaseTools();
    }

}
