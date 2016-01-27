package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hxm.books.R;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.MD5Util;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.ClearEditText;
import com.hxm.books.view.HeaderLayout;

import cn.bmob.v3.listener.SaveListener;


/**
 * Created by hxm on 2016/1/9.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
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
                startAnimActivity(RegisterActivity.class);
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
                login();
                break;
            case R.id.btn_forget_password:

                break;
            default:

                break;
        }
    }

    //登录
    private void login(){
        String name=etLoginAccount.getText().toString().trim();
        String pwd=etLoginPassword.getText().toString().trim();
        MyUser user =new MyUser();
        user.setUsername(name);
        user.setPassword(MD5Util.getMD5String(pwd));
        user.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ToastUtils.show(LoginActivity.this, stringId(LoginActivity.this, R.string.login_success), Toast.LENGTH_SHORT);
                //页面跳转
            }

            @Override
            public void onFailure(int i, String s) {
                if(i==101){
                    ToastUtils.show(LoginActivity.this, stringId(LoginActivity.this, R.string.login_failed), Toast.LENGTH_SHORT);
                }
                LogUtil.e("登录失败",s);
                LogUtil.e("登录失败",i+"");
            }
        });
    }
}
