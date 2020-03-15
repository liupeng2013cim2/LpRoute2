package com.andy.routelib;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.andy.lproute.base.Constants;
import com.andy.lproute.bean.ComponentInfo;
import com.andy.lproute.interfaces.IGroup;

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

    private Context mApplicationContext;



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
            String groupPrefix = Constants.PACKAGE_COMPILE + "." + Constants.GROUP_PREFIX;
            while (enumeration.hasMoreElements()) {
                className = enumeration.nextElement();
                Log.e(TAG, "className:" + className);
                try {
                    if (className.startsWith(groupPrefix)) {
                        Log.e(TAG, "className group:" + className);
                        ((IGroup) (Class.forName(className).newInstance())).loadInfo(sComponentMap);
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
        checkNotInit();

        return new Navigator().new Builder(mApplicationContext, sComponentMap.get(path));
    }





}
