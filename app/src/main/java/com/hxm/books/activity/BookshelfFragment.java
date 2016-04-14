package com.hxm.books.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.hxm.books.config.MyApplication;
import com.hxm.books.R;
import com.hxm.books.adapter.BookShelfAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.listener.DiakogTwoBtnEvent;
import com.hxm.books.listener.FirstDisplayListener;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.utils.cache.FileCache;
import com.hxm.books.view.MyDialog;
import com.hxm.books.view.RefreshLayout;
import com.hxm.books.view.loadingindicator.AVLoadingIndicatorView;
import com.hxm.books.view.swipelistview.SwipeMenu;
import com.hxm.books.view.swipelistview.SwipeMenuCreator;
import com.hxm.books.view.swipelistview.SwipeMenuItem;
import com.hxm.books.view.swipelistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * 书架
 * Created by hxm on 2016/1/25.
 */
public class BookshelfFragment extends Fragment implements View.OnClickListener, RefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    private ImageButton mScanBtn;
    private View view;
    private SwipeMenuListView listview_book;
    private List<Book> bookList = new ArrayList<>();
    private BookShelfAdapter mAdapter;
    private FileCache mCache;
    private AVLoadingIndicatorView loading;
    private RefreshLayout refreshLayout;
    private int pageLimit = 6;
    private int lastPageNum = 0;
    private boolean firstLoad=true;
    private boolean isRefresh=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = MyApplication.cache;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        initView();
        setSwipeMenuListView();
        setListViewData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirstDisplayListener.displayedImages.clear();
    }

    private void initView() {
        mScanBtn = (ImageButton) view.findViewById(R.id.im_btn_scan);
        loading = (AVLoadingIndicatorView) view.findViewById(R.id.loading_view);
        listview_book = (SwipeMenuListView) view.findViewById(R.id.listview_bookshelf);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refresh_layout);
        mScanBtn.setOnClickListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorBase, R.color.colorAccent, R.color.colorPrimary, R.color.colorBrowm);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadingListener(this);

//        refreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.setRefreshing(true);
//            }
//        });
//        //setRefreshing(true) 是不会触发onRefresh的,必须要手动调用一次
//        onRefresh();
    }

    /**
     * 设置左滑从书架中删除藏书
     */
    private void setSwipeMenuListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem menuItem = new SwipeMenuItem(getContext());
                menuItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                menuItem.setWidth(dp2px(100));
                menuItem.setTitle("从书架移除");
                menuItem.setTitleSize(15);
                menuItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(menuItem);

            }
        };

        listview_book.setMenuCreator(creator);
        //左滑的Item项点击事件
        listview_book.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Book item = bookList.get(position);
                bookList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        //左滑开始和结束可执行的事件
        listview_book.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        listview_book.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), BookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookinfo", bookList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listview_book.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MyDialog dialog = new MyDialog(getContext());
                dialog.DialogWithTwoBtn("是否删除", "提示", new DiakogTwoBtnEvent() {
                    @Override
                    public void leftOnClick() {
                        ToastUtils.show(getContext(), "点击左边");
                    }

                    @Override
                    public void rightOnClick() {
                        ToastUtils.show(getContext(), "点击右边");
                    }
                });
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.im_btn_scan:
                intent = new Intent(getActivity(), ScanActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示数据
     */
    private void setListViewData() {
        getBooks();
    }

    /**
     * 获取图书数据
     */
    private void getBooks() {
        if (firstLoad){
            loading.setVisibility(View.VISIBLE);
        }
        BmobQuery<Book> queryBookFromStar = new BmobQuery<>();
        queryBookFromStar.setLimit(6);
        queryBookFromStar.setSkip(lastPageNum);
        MyUser user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        queryBookFromStar.addWhereRelatedTo("likes", new BmobPointer(user));
        queryBookFromStar.findObjects(getActivity(), new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                for (int i=0;i<list.size();i++){
                    bookList.add(list.get(i));
                }
                if (mAdapter==null){
                    mAdapter=new BookShelfAdapter(getActivity(),bookList);
                    listview_book.setAdapter(mAdapter);
                    loading.setVisibility(View.GONE);
                    firstLoad=false;
                }else {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.show(getActivity(), "获取数据失败");
            }
        });
    }

    @Override
    public void onLoad() {
        lastPageNum+=pageLimit;
        isRefresh=false;
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setLoading(false);
                getBooks();
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        lastPageNum=0;
        isRefresh=true;
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                bookList.clear();
                getBooks();
                refreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}