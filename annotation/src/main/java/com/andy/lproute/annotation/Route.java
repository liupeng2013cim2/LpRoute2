package com.andy.lproute.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Route
 * @Description: 表示目标组件对应的路由信息，包含path,name等
 * @Author: andy
 * @Date: 2020/3/14 9:29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Route {
    String path();
    String name();
}
