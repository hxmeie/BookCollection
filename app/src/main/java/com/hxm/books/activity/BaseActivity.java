package com.hxm.books.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.hxm.books.MyApplication;
import com.hxm.books.R;
import com.hxm.books.view.HeaderLayout;

/**
 * 基类
 * Created by hxm on 2015/12/1.
 */
public class BaseActivity extends Activity {

    MyApplication mApplication;
    protected HeaderLayout mHeaderLayout;

    protected int mScreenWidth;
    protected int mScreenHeight;

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = MyApplication.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }

    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
                            Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
            }
        });
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
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

    /** 隐藏软键盘
     * hideSoftInputView
     * @Title: hideSoftInputView
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
