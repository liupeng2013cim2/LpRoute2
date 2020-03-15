package com.andy.routelib;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.andy.lproute.bean.ComponentInfo;

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
