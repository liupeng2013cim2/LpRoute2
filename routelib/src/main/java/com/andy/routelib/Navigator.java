package com.andy.routelib;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.andy.lproute.annotation.Interceptor;
import com.andy.lproute.bean.ComponentInfo;
import com.andy.lproute.bean.InterceptorInfo;
import com.andy.lproute.interfaces.InterceptCallback;
import com.andy.lproute.interfaces.InterceptProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @ClassName: Navigator
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/15 20:43
 */
public class Navigator {
    private static final String TAG = Navigator.class.getSimpleName();
    private ComponentInfo mComponentInfo;
    private String mPath;
    private Context mContext;
    private NavigateCallback mCallback;
    public void navigate() {
        if (mCallback != null) {
            mCallback.onStart();
        }
        if (mComponentInfo == null) {
            if (mCallback != null) {
                mCallback.onFail(new IllegalStateException(String.format("found no path:%s", mPath)));
            }
            return;
        }

        if (TextUtils.isEmpty(mComponentInfo.getPath())) {
            if (mCallback != null) {
                mCallback.onFail(new IllegalArgumentException("path must not be empty"));
            }
            return;
        }

        Map<String, InterceptorInfo> interceptors = RouteManager.getInstance().getInterceptors();
        if (interceptors != null && !interceptors.isEmpty()) {
            intercept(interceptors, new InterceptCallback() {
                @Override
                public void onSuccess() {
                    doSwitch();
                }

                @Override
                public void onFail() {
                    if (mCallback != null) {
                        mCallback.onFail(new IllegalStateException("be intercepted by"));
                    }
                }
            });
        } else {
            doSwitch();
        }

    }

    void doSwitch() {
        if (mContext instanceof Application) {
            Log.e(TAG, "component:" + mComponentInfo.getComponent());
            Intent intent = new Intent(mContext, mComponentInfo.getComponent());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if (mCallback != null) {
                mCallback.onSuccess(mComponentInfo);
            }
        }

        if (mCallback != null) {
            mCallback.onComplete();
        }

        mCallback = null;
    }

    void intercept(Map<String, InterceptorInfo> interceptors, final InterceptCallback callback) {
        if (callback == null || interceptors == null || interceptors.isEmpty()) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(interceptors.size());
        List<Future> futureList = new ArrayList(interceptors.size());
        for (final InterceptorInfo interceptor:interceptors.values()) {
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        Object obj = interceptor.className.newInstance();
                        if (obj instanceof InterceptProcessor) {
                            boolean result = ((InterceptProcessor) obj).process(mComponentInfo, new InterceptCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail() {
                                }
                            });
                            return Boolean.valueOf(result);
                        }
                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
                    } catch (InstantiationException e) {
//                        e.printStackTrace();
                    }
                    return Boolean.FALSE;
                }
            });
            futureList.add(future);
        }
        boolean result = false;
        try {
            for (Future<Boolean> future : futureList) {
                if (future.get()) {
                    result = true;
                    break;
                }
            }
            if (!result) {
                callback.onSuccess();
            } else {
                callback.onFail();
            }
        }catch (InterruptedException | ExecutionException e) {
            callback.onFail();
        } finally {
            executorService.shutdown();
        }
    }

    public interface NavigateCallback {
        void onStart();
        void onSuccess(ComponentInfo componentInfo);
        void onFail(Throwable throwable);
        void onComplete();
    }



    public class Builder {

        public Builder(Context context, String path, ComponentInfo componentInfo) {
            mContext = context;
            mComponentInfo = componentInfo;
            mPath = path;
        }

        public Navigator build() {
            return Navigator.this;
        }

        public Builder callback(NavigateCallback callback) {
            mCallback = callback;
            return this;
        }


    }

}
