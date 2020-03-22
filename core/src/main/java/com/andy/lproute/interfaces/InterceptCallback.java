package com.andy.lproute.interfaces;

import com.andy.lproute.bean.InterceptorInfo;

/**
 * @ClassName: InterceptCallback
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/20 21:13
 */
public interface InterceptCallback {
    void onSuccess(InterceptorInfo interceptProcessor);
    void onFail();
}
