package com.andy.lproute.provider;

import com.andy.lproute.bean.InterceptorInfo;

import java.util.Map;

/**
 * @ClassName: IInterceptor
 * @Description: java����������
 * @Author: andy
 * @Date: 2020/3/20 20:08
 */
public interface IInterceptorInfo {
    void loadInfo(Map<String, InterceptorInfo> map);
}
