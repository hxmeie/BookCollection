package com.hxm.books.view;

import android.content.Context;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.hxm.books.listener.DiakogEvent;

/**
 * 弹出窗口
 * Created by hxm on 2016/3/9.
 */
public class MyDialog {

    private NormalDialog dialog;

    /**
     * @param context 上下文
     */
    public MyDialog(Context context) {
        dialog = new NormalDialog(context);

    }

    /**
     * 默认的Dialog
     * @param content 内容
     * @param event 按钮点击事件
     */
    public void DialogNormal(String content,final DiakogEvent event) {
        dialog.content(content)
                .showAnim(null)
                .dismissAnim(null)
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //左侧按钮事件
                event.leftOnClick();
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //右侧按钮事件
                event.rightOnClick();
                dialog.dismiss();
            }
        });
    }

    /**
     * 自定义标题和内容
     * @param content 内容
     * @param title 标题
     * @param event 点击事件
     */
    public void DialogWithTwoBtn(String content,String title,final DiakogEvent event) {
        dialog.autoDismissDelay(10);
        dialog.content(content)
                .title(title)
                .titleTextSize(20)
                .showAnim(null)
                .dismissAnim(null)
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //左侧按钮事件
                event.leftOnClick();
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //右侧按钮事件
                event.rightOnClick();
                dialog.dismiss();
            }
        });
    }
}
