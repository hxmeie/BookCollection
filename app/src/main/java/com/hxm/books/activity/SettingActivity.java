package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hxm.books.R;
import com.hxm.books.config.AppManager;

import cn.bmob.v3.BmobUser;

/**
 * 设置界面
 * Created by hxm on 2016/3/23.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initOnlyTitleAndLeftBar("设置");
        initView();
    }

    private void initView() {
        btnLogout= (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                BmobUser.logOut(this);
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                AppManager.getAppManager().finishAllActivity();
                break;
        }
    }
}
