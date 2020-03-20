package com.andy.lproute.bean;

/**
 * @ClassName: InterceptorInfo
 * @Description: java¿‡◊˜”√√Ë ˆ
 * @Author: andy
 * @Date: 2020/3/20 20:24
 */
public class InterceptorInfo {
    public Class className;
    public String name;
    public int priority;

    public InterceptorInfo(Class className, String name, int priority) {
        this.className = className;
        this.name = name;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "InterceptorInfo{" +
                "className='" + className.getSimpleName() + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                '}';
    }
}
