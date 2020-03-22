package com.andy.lproute.interceptor;

import android.util.Log;

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
@Interceptor(name="interceptor1", priority = 1)
public class Interceptor1 implements InterceptProcessor {
    private static final String TAG = Interceptor1.class.getSimpleName();

    @Override
    public boolean process(ComponentInfo componentInfo, InterceptCallback callback) {
        if (componentInfo.getComponent().getSimpleName().contains("Test")) {
            Log.e(TAG, "success");
            callback.onSuccess();
            return true;
        } else {
            Log.e(TAG, "fail");
            callback.onFail();
            return false;
        }
    }
}
