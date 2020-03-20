package com.andy.lproute.provider;

import com.andy.lproute.bean.ComponentInfo;

import java.util.Map;

/**
 * @ClassName: IGroup
 * @Description:
 * @Author: andy
 * @Date: 2020/3/15 10:08
 */
public interface IGroup {
    void loadInfo(Map<String, ComponentInfo> map);
}
