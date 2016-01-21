package com.hxm.books.activity;

import android.os.Bundle;
import com.hxm.books.R;

import cn.bmob.v3.Bmob;

/**
 * Created by hxm on 2016/1/13.
 */
public class ActivityMain extends BaseActivity {

    /**
     * SDK初始化建议放在启动页
     */
    public static String APP_ID = "2e03042ef581069f7d0e4ed380a273b6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(getApplicationContext(),APP_ID);
        initView();
    }

    public void initView(){
        initOnlyTitle(stringId(this,R.string.activity_main_title));
    }
}
