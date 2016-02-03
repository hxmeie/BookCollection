package com.hxm.books.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.hxm.books.AppManager;
import com.hxm.books.MyApplication;
import com.hxm.books.R;
import com.hxm.books.view.HeaderLayout;

/**
 * 基类
 * Created by hxm on 2015/12/1.
 */
public class BaseActivity extends FragmentActivity {

    MyApplication mApplication;
    protected HeaderLayout mHeaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = MyApplication.getInstance();
    }


    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    /**
     * 获取string。xml字符串
     * @param id 字符串id
     */
    public String stringId(Context context,int id){
       return context.getResources().getString(id);
    }

    /**
     * 获取string。xml字符串
     * @param id 字符串id
     */
    public String stringId(int id){
        return getResources().getString(id);
    }

    /**
     * 只有标题title
     *
     * @param titleName 标题名称
     */
    public void initOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.initHeaderStytle(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * 只有左侧按钮和标题title
     *
     * @param titleName 标题名称
     */
    public void initOnlyTitleAndLeftBar(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.initHeaderStytle(HeaderLayout.HeaderStyle.TITLE_LEFT_IMAGE_BTN);
        mHeaderLayout.setTitleAndLeftBtn(titleName, R.drawable.base_action_bar_back_bg_selector, new onLeftCilckListener());
    }

    /**
     * 带左右按钮和标题栏title
     *
     * @param titleName 标题名称
     * @param rightId   右侧按钮资源ID
     * @param listener  右侧按钮点击事件监听
     */
    public void initBothLeftAndRightBar(String titleName, int rightId, HeaderLayout.headerLayoutRightOnclickLister listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.initHeaderStytle(HeaderLayout.HeaderStyle.TITLE_BOTH_LEFT_AND_RIGHT);
        mHeaderLayout.setTitleAndLeftBtn(titleName, R.drawable.base_action_bar_back_bg_selector, new onLeftCilckListener());
        mHeaderLayout.setTitleAndRightBtn(titleName, rightId, listener);
    }

    /**
     * 带左右按钮和标题栏title
     *
     * @param titleName 标题名称
     * @param text      右侧按钮文本
     * @param rightId   右侧按钮资源ID
     * @param listener  右侧按钮点击事件监听
     */
    public void initBothLeftAndRightBar(String titleName, String text, int rightId, HeaderLayout.headerLayoutRightOnclickLister listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.initHeaderStytle(HeaderLayout.HeaderStyle.TITLE_BOTH_LEFT_AND_RIGHT);
        mHeaderLayout.setTitleAndLeftBtn(titleName, R.drawable.base_action_bar_back_bg_selector, new onLeftCilckListener());
        mHeaderLayout.setTitleAndRightBtn(titleName, text, rightId, listener);
    }

    /**
     * 左侧按钮点击事件
     */
    public class onLeftCilckListener implements HeaderLayout.headerLayoutLeftOnclickLister {

        @Override
        public void onClick() {
            finish();
        }
    }

    /**
     * 右侧按钮点击事件
     */
    public class onRightClickListener implements HeaderLayout.headerLayoutRightOnclickLister{

        @Override
        public void onClick() {

        }
    }
}
