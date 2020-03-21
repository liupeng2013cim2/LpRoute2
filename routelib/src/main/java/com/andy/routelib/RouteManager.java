package com.andy.routelib;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.andy.lproute.base.Constants;
import com.andy.lproute.bean.ComponentInfo;
import com.andy.lproute.bean.InterceptorInfo;
import com.andy.lproute.provider.IGroup;
import com.andy.lproute.provider.IInterceptorInfo;
import com.andy.lproute.util.RouteUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * @ClassName: Route
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/15 18:34
 */
public class RouteManager {

    private static final String TAG = RouteManager.class.getSimpleName();

    private static final int INIT_CAPACITY = 10;

    private Map<String, ComponentInfo> sComponentMap = new HashMap(INIT_CAPACITY);
    private Map<String, InterceptorInfo> sInterceptorMap = new HashMap(INIT_CAPACITY);

    private Context mApplicationContext;


    private static class Holder {
        static RouteManager instance = new RouteManager();
    }

    public static RouteManager getInstance() {
        return Holder.instance;
    }

    private RouteManager() {
    }

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
            Enumeration<String> enumeration = dexFile.entries();
            String className = null;
            String groupPrefix = Constants.PACKAGE_COMPILE + "." + Constants.GROUP_PREFIX;
            String interceptorHolder = Constants.PACKAGE_COMPILE + "." + Constants.CLASS_INTERCEPTOR;
            while (enumeration.hasMoreElements()) {
                className = enumeration.nextElement();
                Log.e(TAG, "className:" + className);
                try {
                    if (className.startsWith(groupPrefix)) {
                        Log.e(TAG, "className group:" + className);
                        ((IGroup) (Class.forName(className).newInstance())).loadInfo(sComponentMap);
                    } else if (className.equals(interceptorHolder)) {
                        ((IInterceptorInfo)(Class.forName(className).newInstance())).loadInfo(sInterceptorMap);
                    }
                } catch (ClassNotFoundException e) {

                } catch (InstantiationException | IllegalAccessException e) {

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Navigator.Builder path(String path) {
        return new Navigator().new Builder(mApplicationContext, path, sComponentMap.get(path));
    }

    public Navigator.Builder path(String path, Navigator.NavigateCallback callback) {

        return new Navigator().new Builder(mApplicationContext, path, sComponentMap.get(path)).callback(callback);
    }

    public Map<String, InterceptorInfo> getInterceptors() {
        return sInterceptorMap;
    }

    public ComponentInfo getComponent(String path) {
        return sComponentMap.get(path);
    }

    public int startActivityForResult(Activity context, String path, Intent intent, int requestCode) {
        ComponentInfo componentInfo = sComponentMap.get(path);
        if (componentInfo == null) {
            return -1;
        }
        Class componentCls = componentInfo.getComponent();
        ComponentName componentName = new ComponentName(RouteUtils.getPackageName(componentCls), componentCls.getSimpleName()
        );
        intent.setComponent(componentName);
        context.startActivityForResult(intent, requestCode);
        return 1;
    }


}
