package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;

import com.hxm.books.R;
import com.hxm.books.view.HeaderLayout;

/**
 * Created by hxm on 2016/1/9.
 */
public class ActivityLogin extends BaseActivity implements HeaderLayout.headerLayoutRightOnclickLister,View.OnClickListener{
    private HeaderLayout.headerLayoutRightOnclickLister mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView(){
        initBothLeftAndRightBar(stringId(this, R.string.login_title),stringId(this,R.string.login_title_right_button_text),R.color.transparent,mListener);

    }

    @Override
    public void onClick() {
        startAnimActivity(ActivityRegister.class);
    }

    @Override
    public void onClick(View v) {

    }
}
