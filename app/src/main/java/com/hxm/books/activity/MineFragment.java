package com.hxm.books.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hxm.books.R;

/**
 * Created by hxm on 2016/1/25.
 * 个人信息界面
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    private ImageView ivHeader;
    private LinearLayout personalInfo;
    private RelativeLayout setting;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        ivHeader= (ImageView) view.findViewById(R.id.iv_user_header);
        personalInfo= (LinearLayout) view.findViewById(R.id.fg_mine_personal_info);
        setting= (RelativeLayout) view.findViewById(R.id.fg_mine_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_user_header:

                break;
            case R.id.fg_mine_personal_info:

                break;
            case R.id.fg_mine_setting:

                break;
        }
    }
}
