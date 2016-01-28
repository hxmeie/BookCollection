package com.hxm.books.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.hxm.books.R;

import java.lang.ref.WeakReference;

import cn.bmob.v3.Bmob;

/**
 * 应用启动时的引导页
 * Created by hxm on 2015/12/1.
 */
public class SplashActivity extends BaseActivity{
    private static final int GO_MAIN=100;
    private MyHandler handler =new MyHandler(this);
    /**
     * SDK初始化建议放在启动页
     */
    public static String APP_ID = "2e03042ef581069f7d0e4ed380a273b6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Bmob.initialize(getApplicationContext(),APP_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(GO_MAIN,2000);
    }

    static class MyHandler extends Handler{
        WeakReference<SplashActivity> mActivity;
        public MyHandler(SplashActivity activity){
            mActivity=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity splashActivity =mActivity.get();
            switch (msg.what){
                case GO_MAIN:
                    splashActivity.startAnimActivity(MainActivity.class);
                    splashActivity.finish();
                    break;
            }
        }
    }
}
