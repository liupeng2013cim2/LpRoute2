package com.andy.lproute.interfaces;

import com.andy.lproute.bean.ComponentInfo;

import java.util.HashMap;
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
