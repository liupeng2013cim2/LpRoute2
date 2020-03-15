package com.andy.routelib;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
    private Context mContext;
    public void navigate() {
        if (mContext instanceof Application) {
            Log.e(TAG, "component:" + mComponentInfo.getComponent());
            Intent intent = new Intent(mContext, mComponentInfo.getComponent());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }



    public class Builder {

        public Builder(Context context, ComponentInfo componentInfo) {
            mContext = context;
            mComponentInfo = componentInfo;
        }

        public Navigator build() {
            return Navigator.this;
        }

    }

}
