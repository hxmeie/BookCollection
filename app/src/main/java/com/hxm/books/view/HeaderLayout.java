package com.hxm.books.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hxm.books.R;

/**
 * 自定义标题栏布局
 * Created by hxm on 2016/1/11.
 */
public class HeaderLayout extends LinearLayout{
    private LayoutInflater mInflater;
    private View mHeader;
    private LinearLayout mLayoutLeftContainer;
    private LinearLayout mLayoutRightContainer;
    private TextView mHeaderTitle;

    private LinearLayout mLayoutLeftImageButtonLayout;
    private headerLayoutLeftOnclickLister mLeftOnClickListener;
    private ImageButton mHeaderLeftImageBtn;

    private LinearLayout mLayoutRightTextViewLayout;
    private headerLayoutRightOnclickLister mRightOnClickListener;
    private TextView mHeaderRightTextView;

    public HeaderLayout(Context context) {
        super(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context){
        mInflater=LayoutInflater.from(context);
        mHeader=mInflater.inflate(R.layout.common_header,null);
    }

    public void setHeaderLayoutLeftOnClickListener(headerLayoutLeftOnclickLister listener){
        mLeftOnClickListener=listener;
    }

    public interface headerLayoutLeftOnclickLister{
       void onClick();
    }

    public void setHeaderLayoutRightOnClickListener(headerLayoutRightOnclickLister listener){
        mRightOnClickListener=listener;
    }

    public interface headerLayoutRightOnclickLister{
        void onClick();
    }
}
