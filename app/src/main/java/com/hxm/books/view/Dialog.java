package com.hxm.books.view;

import android.content.Context;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

/**
 * 弹出窗口
 * Created by hxm on 2016/3/9.
 */
public class Dialog {

    private NormalDialog dialog;
    private String content;
    private String title;

    public Dialog(Context context) {
        dialog=new NormalDialog(context);
    }

    public Dialog(Context context, String content, String title) {
        dialog=new NormalDialog(context);
        this.content = content;
        this.title = title;
    }

    public void DialogWithTwoBtn(final DialogBtnEvent event){
        dialog.autoDismissDelay(10);
        dialog.content(content)
                .title(title)
                .titleTextSize(23)
                .showAnim(null)
                .autoDismissDelay(100)
                .dismissAnim(null)
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //左侧按钮事件
                event.leftBtn();
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //右侧按钮事件
                event.rightBtn();
                dialog.dismiss();
            }
        });
    }

    public interface DialogBtnEvent{
        void leftBtn();
        void rightBtn();
    }
}
