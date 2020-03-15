package com.andy.lproute.util;

/**
 * @ClassName: RouteUtils
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/15 22:01
 */
public class RouteUtils {

    public static String getPackageName(Class cls) {
        int lastDotIndex = cls.getName().lastIndexOf(".");
        return cls.getName().substring(0, lastDotIndex);
    }
}
