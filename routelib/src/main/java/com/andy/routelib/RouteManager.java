package com.andy.routelib;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * @ClassName: Route
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/15 18:34
 */
public class RouteManager {

    private static final String TAG = RouteManager.class.getSimpleName();

    private static Context mApplicationContext;

    private static class Holder {
        static RouteManager instance = new RouteManager();
    }

    public static RouteManager getInstance() {
        return Holder.instance;
    }

    private RouteManager() {}

    public void init(Application application) {
        checkInit();
        mApplicationContext = application;
        Log.e(TAG, "init");
        initComponents();
    }

    private void checkInit() {
        if (mApplicationContext != null) {
            throw new IllegalStateException("RouteManager has been initialized already");
        }
    }

    private void checkNotInit() {
        if (mApplicationContext == null) {
            throw new IllegalStateException("RouteManager has been initialized already");
        }
    }

    public boolean hasInit() {
        return mApplicationContext != null;
    }

    private void initComponents() {
        checkNotInit();
        ApplicationInfo applicationInfo = mApplicationContext.getApplicationInfo();

        try {
            DexFile dexFile = new DexFile(applicationInfo.sourceDir);
            Enumeration<String> enumeration =dexFile.entries();
            String className = null;
            while (enumeration.hasMoreElements()) {
                className = enumeration.nextElement();
                Log.e(TAG, "className:" + className);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
