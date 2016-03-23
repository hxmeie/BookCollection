package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.Constants;
import com.hxm.books.config.MyApplication;

import cn.bmob.v3.BmobUser;

/**
 * Created by hxm on 2016/1/25.
 * 个人信息界面
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    private ImageView ivHeader;
    private LinearLayout personalInfo;
    private RelativeLayout setting;
    private TextView tvNickName,tvUserName;
    private MyUser user=BmobUser.getCurrentUser(MyApplication.getInstance(), MyUser.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view){
        ivHeader= (ImageView) view.findViewById(R.id.iv_user_header);
        personalInfo= (LinearLayout) view.findViewById(R.id.fg_mine_personal_info);
        setting= (RelativeLayout) view.findViewById(R.id.fg_mine_setting);
        tvNickName= (TextView) view.findViewById(R.id.tv_nick_name);
        tvUserName= (TextView) view.findViewById(R.id.tv_login_name);

        personalInfo.setOnClickListener(this);
        ivHeader.setOnClickListener(this);
    }

    private void initData(){
        tvUserName.setText(user.getUsername());
        tvNickName.setText(user.getNickName());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.iv_user_header:

            case R.id.fg_mine_personal_info:
                intent=new Intent(getActivity(),UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.fg_mine_setting:

                break;
        }
    }

}
