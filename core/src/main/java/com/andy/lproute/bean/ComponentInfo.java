package com.andy.lproute.bean;

/**
 * @ClassName: ComponentInfo
 * @Description: java
 * @Author: andy
 * @Date: 2020/3/15 10:11
 */
public class ComponentInfo {
    private String mPath;
    private Class mClass;

    public ComponentInfo(String path, Class cls) {
        mPath = path;
        mClass = cls;
    }

}
