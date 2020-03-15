package com.andy.routelib;

/**
 * @ClassName: Route
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/15 18:34
 */
public class RouteManager {

    private static class Holder {
        static RouteManager instance = new RouteManager();
    }

    private RouteManager() {}


    public static RouteManager getInstance() {
        return Holder.instance;
    }


}
