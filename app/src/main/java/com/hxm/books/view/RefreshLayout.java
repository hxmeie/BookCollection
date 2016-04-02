package com.hxm.books.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.hxm.books.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 继承自SwipeRefreshLayout，实现滑动到底部上拉加载更多
 * <p/>
 * Created by hxm on 2016/3/31.
 */
public class RefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {

    /**
     * TouchSlope是系统所能识别出的可以被认为是滑动的最小距离
     */
    private int mTouchSlop;

    /**
     * ListView实例
     */
    private ListView mListView;

    /**
     * 上拉监听器
     */
    private OnLoadListener mOnLoadListener;

    /**
     * ListView加载的footer
     */
    private View mListViewFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;

    /**
     * 抬起时的y坐标,根据(mYDown - mLastY) >= mTouchSlop判断是否是上拉操作
     */
    private int mLastY;

    /**
     * 判断是否在加载（上拉）
     */
    private boolean isLoading = false;


    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null, false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //初始化ListView对象
        if (mListView == null) {
            getListView();
        }
    }

    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                //设置滑动监听
                mListView.setOnScrollListener(this);
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                mLastY = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否可以加载更多，条件为滑到底部，ListView不在加载中，且为上拉操作
     *
     * @return true or false
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否为上拉
     *
     * @return true or false
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 判断ListView是否滑动到了最底部
     *
     * @return true or false
     */
    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            //设置加载状态
            setLoading(true);
            mOnLoadListener.onLoad();
        }
    }

    /**
     * 设置loading状态
     *
     * @param loading true or false
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            mListView.addFooterView(mListViewFooter);
        } else {
            mListView.removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * 设置监听
     *
     * @param onLoadListener 监听
     */
    public void setOnLoadingListener(OnLoadListener onLoadListener) {
        mOnLoadListener = onLoadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    /**
     * 设置刷新
     */
    public static void setRefreshing(SwipeRefreshLayout swipeRefreshLayout, boolean refreshing, boolean notify) {
        Class<? extends SwipeRefreshLayout> refreshLayoutClass = swipeRefreshLayout.getClass();
        if (refreshLayoutClass != null) {
            try {
                Method setRefreshing = refreshLayoutClass.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
                setRefreshing.setAccessible(true);
                setRefreshing.invoke(swipeRefreshLayout, refreshing, notify);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载更多监听器
     *
     * @author hxm
     */
    public interface OnLoadListener {
        void onLoad();
    }
}
