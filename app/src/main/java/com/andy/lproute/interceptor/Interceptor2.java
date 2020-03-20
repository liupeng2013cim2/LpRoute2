package com.andy.lproute.interceptor;

import com.andy.lproute.annotation.Interceptor;
import com.andy.lproute.bean.ComponentInfo;
import com.andy.lproute.interfaces.InterceptCallback;
import com.andy.lproute.interfaces.InterceptProcessor;

/**
 * @ClassName: Interceptor1
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/20 21:11
 */
@Interceptor(name="interceptor2", priority = 2)
public class Interceptor2 implements InterceptProcessor {

    @Override
    public boolean process(ComponentInfo componentInfo, InterceptCallback callback) {
        if (componentInfo.getComponent().getSimpleName().contains("Main")) {
            callback.onSuccess();
            return true;
        } else {
            callback.onFail();
            return false;
        }
    }
}
