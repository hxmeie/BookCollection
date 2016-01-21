package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hxm.books.R;
import com.hxm.books.view.ClearEditText;
import com.hxm.books.view.HeaderLayout;


/**
 * Created by hxm on 2016/1/9.
 */
public class ActivityLogin extends BaseActivity implements View.OnClickListener{
    private ClearEditText etLoginAccount,etLoginPassword;
    private Button btnLogin,btnForgetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

    }

    public void initView(){
//       初始化标题栏
        initBothLeftAndRightBar(stringId(this, R.string.login_title), stringId(this, R.string.login_title_right_button_text), R.color.transparent, new HeaderLayout.headerLayoutRightOnclickLister() {
            @Override
            public void onClick() {
                startAnimActivity(ActivityRegister.class);
            }
        });

        etLoginAccount= (ClearEditText) findViewById(R.id.et_login_account);
        etLoginPassword= (ClearEditText) findViewById(R.id.et_login_password);
        btnLogin= (Button) findViewById(R.id.btn_login);
        btnForgetPassword= (Button) findViewById(R.id.btn_forget_password);

        btnLogin.setOnClickListener(this);
        btnForgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:

                break;
            case R.id.btn_forget_password:

                break;
            default:

                break;
        }
    }
}
