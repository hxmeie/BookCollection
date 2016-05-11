package com.hxm.books.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.listener.TextAndArrowListener;

/**
 * Created by hxm on 2016/1/13.
 */


public class NetUtil {

    /** 检查是否有网络 */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /** 检查是否是WIFI */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }
        return false;
    }

    /** 检查是否是移动网络 */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    private static NetworkInfo getNetworkInfo(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /** 检查SD卡是否存在 */
    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    /**
     * 设置TextView折叠效果
     */
    public static void setTextAnim(final TextView textView, final ImageView imageView, final int maxLine){
        //设置默认显示高度
        textView.setHeight(textView.getLineHeight()*maxLine);
        //根据高度来控制是否展示翻转icon
        textView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(textView.getLineCount()>maxLine? View.VISIBLE:View.GONE);
            }
        });

        textView.setOnClickListener(new TextAndArrowListener(textView,imageView,maxLine));
    }
}
