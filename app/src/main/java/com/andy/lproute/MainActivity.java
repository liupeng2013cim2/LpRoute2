package com.andy.lproute;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andy.lproute.annotation.Route;
import com.andy.lproute.constants.PathConstants;

/**
 * @ClassName: MainActivity
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/14 8:51
 */
@Route(path = PathConstants.PATH_MAIN, name="main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
