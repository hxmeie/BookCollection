package com.hxm.books;

import android.app.Application;

import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;

import org.kymjs.kjframe.bitmap.BitmapConfig;

import cn.bmob.v3.BmobUser;

/**
 * 自定义全局类
 * Created by hxm on 2016/1/12.
 */
public class MyApplication extends Application {

    public static MyApplication mInstance;
    public static MyUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //设置是否打印log
        LogUtil.isDebug=true;
        user = BmobUser.getCurrentUser(getInstance(),MyUser.class);
    }


    public static MyApplication getInstance() {
        return mInstance;
    }
}
