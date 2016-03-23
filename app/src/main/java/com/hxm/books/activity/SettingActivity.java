package com.hxm.books.activity;

import android.os.Bundle;

import com.hxm.books.R;

/**
 * 设置界面
 * Created by hxm on 2016/3/23.
 */
public class SettingActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initOnlyTitleAndLeftBar("设置");
    }
}
