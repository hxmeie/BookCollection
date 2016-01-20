package com.hxm.books.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.hxm.books.R;

import java.lang.ref.WeakReference;

/**
 * 应用启动时的引导页
 * Created by hxm on 2015/12/1.
 */
public class ActivitySplash extends BaseActivity{
    private static final int GO_MAIN=100;
    private MyHandler handler =new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(GO_MAIN,2000);
    }

    static class MyHandler extends Handler{
        WeakReference<ActivitySplash> mActivity;
        public MyHandler(ActivitySplash activity){
            mActivity=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActivitySplash activitySplash=mActivity.get();
            switch (msg.what){
                case GO_MAIN:
                    activitySplash.startAnimActivity(ActivityLogin.class);
                    activitySplash.finish();
                    break;
            }
        }
    }
}
