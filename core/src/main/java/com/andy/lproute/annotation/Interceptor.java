package com.andy.lproute.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Interceptor
 * @Description: java¿‡◊˜”√√Ë ˆ
 * @Author: andy
 * @Date: 2020/3/20 20:07
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Interceptor {
    String name();
    int priority();
}
