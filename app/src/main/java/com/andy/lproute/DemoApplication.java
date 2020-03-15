package com.andy.lproute;

import android.app.Application;

import com.andy.routelib.RouteManager;

/**
 * @ClassName: DemoApplication
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/15 18:36
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RouteManager.getInstance().init(this);
    }
}
