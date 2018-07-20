package com.lch.annotationtool;

import com.lch.autoConfig.LchAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({LchAutoConfiguration.class})
public @interface EnableStartLch {

}
