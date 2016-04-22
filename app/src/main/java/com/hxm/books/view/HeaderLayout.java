package com.hxm.books.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.utils.PixelUtil;

/**
 * 自定义标题栏布局
 * Created by hxm on 2016/1/11.
 */
public class HeaderLayout extends LinearLayout {
    private LayoutInflater mInflater;
    private View mHeader;
    private LinearLayout mLayoutLeftContainer;
    private LinearLayout mLayoutRightContainer;
    private LinearLayout mLayoutMiddleContainer;
    private TextView mHeaderTitle;

    private LinearLayout mLayoutLeftImageButtonLayout;
    private headerLayoutLeftOnclickLister mLeftOnClickListener;
    private ImageButton mHeaderLeftImageBtn;

    private LinearLayout mLayoutRightTextViewLayout;
    private headerLayoutRightOnclickLister mRightOnClickListener;

    private Button mHeaderRightButton;
    private ClearEditText mEditText;
    public String searchKeyWord=null;
    View middleSearchEditText;

    //标题栏样式
    public enum HeaderStyle {
        DEFAULT_TITLE, TITLE_LEFT_IMAGE_BTN, TITLE_RIGHT_TV, TITLE_BOTH_LEFT_AND_RIGHT, LEFT_SEARCH_AND_RIGHT
    }

    public HeaderLayout(Context context) {
        super(context);
        init(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeader = mInflater.inflate(R.layout.common_header, null);
        middleSearchEditText = mInflater.inflate(R.layout.common_header_search_view, null);
        mEditText = (ClearEditText) middleSearchEditText.findViewById(R.id.et_search);
        searchKeyWord=mEditText.getText().toString().trim();
        addView(mHeader);
        initViews();
    }

    public void initViews() {
        mLayoutLeftContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_leftview_container);
        mLayoutRightContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_rightview_container);
        mLayoutMiddleContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_middleview_container);
        mHeaderTitle = (TextView) findViewByHeaderId(R.id.tv_header_title);
    }

    public View findViewByHeaderId(int id) {

        return mHeader.findViewById(id);

    }

    public void initHeaderStytle(HeaderStyle mStyle) {
        switch (mStyle) {
            case DEFAULT_TITLE:
                defaultTitle();
                break;
            case TITLE_LEFT_IMAGE_BTN:
                defaultTitle();
                titleLeftImageBtn();
                break;
            case TITLE_RIGHT_TV:
                defaultTitle();
                titleRightTextViewBtn();
                break;
            case TITLE_BOTH_LEFT_AND_RIGHT:
                defaultTitle();
                titleLeftImageBtn();
                titleRightTextViewBtn();
                break;
            case LEFT_SEARCH_AND_RIGHT:
                defaultTitle();
                titleLeftImageBtn();
                middleSearchView();
                titleRightTextViewBtn();
                break;
        }
    }

    //清除title信息
    public void defaultTitle() {
        mLayoutRightContainer.removeAllViews();
        mLayoutLeftContainer.removeAllViews();
        mLayoutMiddleContainer.removeAllViews();
    }

    //左侧按钮
    public void titleLeftImageBtn() {
        View mLeftImageViewButton = mInflater.inflate(R.layout.common_header_left_button, null);
        mLayoutLeftContainer.addView(mLeftImageViewButton);
        mLayoutLeftImageButtonLayout = (LinearLayout) mLeftImageViewButton.findViewById(R.id.header_layout_left_imagebutton_layout);
        mHeaderLeftImageBtn = (ImageButton) mLeftImageViewButton.findViewById(R.id.header_left_imagebutton);
        mLayoutLeftImageButtonLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftOnClickListener != null) {
                    mLeftOnClickListener.onClick();
                }
            }
        });
    }

    //右侧按钮
    public void titleRightTextViewBtn() {
        View mRightTextViewButton = mInflater.inflate(R.layout.common_header_right_button, null);
        mLayoutRightContainer.addView(mRightTextViewButton);
        mLayoutRightTextViewLayout = (LinearLayout) mRightTextViewButton.findViewById(R.id.header_layout_right_text_layout);
        mHeaderRightButton = (Button) mRightTextViewButton.findViewById(R.id.tv_header_right_button);
        mLayoutRightTextViewLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightOnClickListener != null) {
                    mRightOnClickListener.onClick();
                }
            }
        });
    }

    //中间搜索框
    public void middleSearchView() {
        //View middleSearchEditText = mInflater.inflate(R.layout.common_header_search_view, null);
        mLayoutMiddleContainer.addView(middleSearchEditText);
//        mEditText = (ClearEditText) middleSearchEditText.findViewById(R.id.et_search);
    }

    //获取EditText
    public ClearEditText getSearchEditText() {
       return mEditText;
    }

    //获取右侧按钮
    public Button getHeaderRightButton() {
        if (mHeaderRightButton != null) {
            return mHeaderRightButton;
        }
        return null;
    }

    //设置title
    public void setDefaultTitle(CharSequence title) {
        if (title != null) {
            mHeaderTitle.setText(title);
        } else {
            mHeaderTitle.setVisibility(View.GONE);
        }
    }

    //设置左侧按钮，中间搜索框，右侧按钮
    public void setSearch(String rightBtnText, int rightBackColorId, int resId, headerLayoutLeftOnclickLister leftListener, headerLayoutRightOnclickLister rightListener) {
        if (mHeaderLeftImageBtn != null && resId > 0) {
            mHeaderLeftImageBtn.setImageResource(resId);
            setHeaderLayoutLeftOnClickListener(leftListener);
        }
        if (mHeaderRightButton != null && rightBackColorId > 0) {
            mHeaderRightButton.setWidth(PixelUtil.dp2px(45));
            mHeaderRightButton.setHeight(PixelUtil.dp2px(40));
            mHeaderRightButton.setText(rightBtnText);
            mHeaderRightButton.setBackgroundResource(rightBackColorId);
            setHeaderLayoutRightOnClickListener(rightListener);
        }
    }

    //设置左侧按钮和title
    public void setTitleAndLeftBtn(CharSequence title, int resId, headerLayoutLeftOnclickLister listener) {
        setDefaultTitle(title);
        if (mHeaderLeftImageBtn != null && resId > 0) {
            mHeaderLeftImageBtn.setImageResource(resId);
            setHeaderLayoutLeftOnClickListener(listener);
        }
        mLayoutRightContainer.setVisibility(INVISIBLE);
    }

    //设置右侧按钮和title
    public void setTitleAndRightBtn(CharSequence title, String text, int backId, headerLayoutRightOnclickLister listener) {
        mLayoutRightContainer.setVisibility(VISIBLE);
        setDefaultTitle(title);
        if (mHeaderRightButton != null && backId > 0) {
            mHeaderRightButton.setWidth(PixelUtil.dp2px(45));
            mHeaderRightButton.setHeight(PixelUtil.dp2px(40));
            mHeaderRightButton.setText(text);
            mHeaderRightButton.setBackgroundResource(backId);
            setHeaderLayoutRightOnClickListener(listener);
        }
    }

    public void setTitleAndRightBtn(CharSequence title, int backId, headerLayoutRightOnclickLister listener) {
        mLayoutRightContainer.setVisibility(VISIBLE);
        setDefaultTitle(title);
        if (mHeaderRightButton != null && backId > 0) {
            mHeaderRightButton.setWidth(PixelUtil.dp2px(30));
            mHeaderRightButton.setHeight(PixelUtil.dp2px(30));
            mHeaderRightButton.setTextColor(getResources().getColor(R.color.transparent));
            mHeaderRightButton.setBackgroundResource(backId);
            setHeaderLayoutRightOnClickListener(listener);
        }
    }

    public void setHeaderLayoutLeftOnClickListener(headerLayoutLeftOnclickLister listener) {
        mLeftOnClickListener = listener;
    }

    public interface headerLayoutLeftOnclickLister {
        void onClick();
    }

    public void setHeaderLayoutRightOnClickListener(headerLayoutRightOnclickLister listener) {
        mRightOnClickListener = listener;
    }

    public interface headerLayoutRightOnclickLister {
        void onClick();
    }
}
