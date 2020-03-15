package com.andy.lproute;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andy.lproute.annotation.Route;
import com.andy.lproute.bean.ComponentInfo;
import com.andy.lproute.constants.PathConstants;
import com.andy.routelib.Navigator;
import com.andy.routelib.RouteManager;

/**
 * @ClassName: MainActivity
 * @Description: java类作用描述
 * @Author: andy
 * @Date: 2020/3/14 8:51
 */
@Route(path = PathConstants.PATH_MAIN, name="main")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteManager.getInstance().path("/andy/test1")
                        .callback(new Navigator.NavigateCallback() {
                            @Override
                            public void onStart() {
                                Log.e(TAG, "onStart");
                            }

                            @Override
                            public void onSuccess(ComponentInfo componentInfo) {
                                Log.e(TAG, "onSuccess, componentInfo:" + componentInfo);
                            }

                            @Override
                            public void onFail(Throwable throwable) {
                                Log.e(TAG, "onFail, throw:" + throwable.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.e(TAG, "onComplete");
                            }
                        })
                .build()
                .navigate();
            }
        });
    }
}
