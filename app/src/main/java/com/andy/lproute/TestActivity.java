package com.andy.lproute;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.andy.lproute.annotation.Route;

/**
 * @ClassName: TestActivity
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/15 20:31
 */
@Route(path = "/andy/test", name="test")
public class TestActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
