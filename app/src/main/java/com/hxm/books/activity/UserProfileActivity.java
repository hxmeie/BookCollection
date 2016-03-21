package com.hxm.books.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.Constants;
import com.hxm.books.config.MyApplication;

import cn.bmob.v3.BmobUser;

/**
 * user profile
 * Created by hxm on 2016/3/17.
 */
public class UserProfileActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvNickName,tvUserName,tvSex,tvSignUpTime;
    private ImageView ivHeader;
    private RelativeLayout layoutHeader,layoutNickname,layoutSex;
    private MyUser user= BmobUser.getCurrentUser(MyApplication.getInstance(),MyUser.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initView();
        initData();
    }

    private void initView(){
        initOnlyTitleAndLeftBar(stringId(R.string.my_profile));
        tvNickName= (TextView) findViewById(R.id.tv_profile_nickname);
        tvUserName= (TextView) findViewById(R.id.tv_profile_username);
        tvSex= (TextView) findViewById(R.id.tv_profile_sex);
        tvSignUpTime= (TextView) findViewById(R.id.tv_profile_sign_up_time);
        layoutHeader= (RelativeLayout) findViewById(R.id.profile_header_layout);
        layoutNickname= (RelativeLayout) findViewById(R.id.profile_nickname_layout);
        layoutSex= (RelativeLayout) findViewById(R.id.profile_sex_layout);

        layoutHeader.setOnClickListener(this);
        layoutNickname.setOnClickListener(this);
        layoutSex.setOnClickListener(this);

    }

    private void initData(){
        tvNickName.setText(user.getNickName());
        tvUserName.setText(user.getUsername());
        tvSignUpTime.setText(user.getCreatedAt());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_header_layout:

                break;
            case R.id.profile_nickname_layout:

                break;
            case R.id.profile_sex_layout:

                break;
        }
    }
}
