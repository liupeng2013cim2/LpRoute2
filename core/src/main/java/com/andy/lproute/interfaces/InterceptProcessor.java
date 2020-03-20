package com.andy.lproute.interfaces;

import com.andy.lproute.bean.ComponentInfo;

/**
 * @ClassName: InterceptProcessor
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/20 21:09
 */
public interface InterceptProcessor {

    boolean process(ComponentInfo componentInfo, InterceptCallback callback);

}
