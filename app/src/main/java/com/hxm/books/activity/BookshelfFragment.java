package com.hxm.books.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.hxm.books.config.Constants;
import com.hxm.books.config.MyApplication;
import com.hxm.books.R;
import com.hxm.books.adapter.BookShelfAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.BookToUser;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.cache.FileCache;
import com.hxm.books.utils.cache.FileCacheManger;
import com.hxm.books.view.listview.SwipeMenu;
import com.hxm.books.view.listview.SwipeMenuCreator;
import com.hxm.books.view.listview.SwipeMenuItem;
import com.hxm.books.view.listview.SwipeMenuRefreshListView;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by hxm on 2016/1/25.
 */
public class BookshelfFragment extends Fragment implements View.OnClickListener, SwipeMenuRefreshListView.IXListViewListener {
    private ImageButton mScanBtn;
    private View view;
    private SwipeMenuRefreshListView listBookshelf;
    private MyUser user = MyApplication.user;
    private List<Book> bookList;
    private List<BookToUser> bookToUserList = new ArrayList<>();
    private BookShelfAdapter mAdapter;
    private Handler mHandler;
    private FileCache mCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = MyApplication.cache;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        mHandler = new Handler();
        initView();
        setSwipeMenuListView();
        getBookList();
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
        listBookshelf = (SwipeMenuRefreshListView) view.findViewById(R.id.list_bookshelf);
        listBookshelf.setPullLoadEnable(true);
        listBookshelf.setPullRefreshEnable(true);
        listBookshelf.setXListViewListener(this);
        mScanBtn.setOnClickListener(this);
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

        listBookshelf.setMenuCreator(creator);

        listBookshelf.setOnMenuItemClickListener(new SwipeMenuRefreshListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Book item = bookList.get(position);
                bookList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        listBookshelf.setOnSwipeListener(new SwipeMenuRefreshListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        listBookshelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), BookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookinfo", bookList.get(position - 1));
                intent.putExtras(bundle);
                startActivity(intent);
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

    private void getBookList() {
        bookList = new ArrayList<>();
        if (FileCacheManger.isExistDataCache(getContext(), Constants.CACHE_BOOK_LIST.hashCode()+"")) {
            String jsonString = mCache.getAsString("book_list");
            bookList=JSON.parseArray(jsonString,Book.class);
            LogUtil.i("getBookData","从缓存中获取");
        } else {
            LogUtil.i("getBookData","从网络中获取");
            String sql = "select * from Book";
            BmobQuery<Book> query = new BmobQuery<>();
            query.setSQL(sql);
            query.doSQLQuery(getContext(), new SQLQueryListener<Book>() {
                @Override
                public void done(BmobQueryResult<Book> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<Book> books = bmobQueryResult.getResults();
                        for (int i = 0; i < books.size(); i++) {
                            bookList.add(books.get(i));
                        }

                    }
                    String bookJson=JSON.toJSONString(bookList,true);
                    LogUtil.i("getBookData", Constants.CACHE_BOOK_LIST.hashCode()+"");
                    mCache.put("book_list",bookJson);
                }
            });
        }
        mAdapter = new BookShelfAdapter(getContext(), bookList);
        listBookshelf.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listBookshelf.stopRefresh();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String time = format.format(curDate);
                listBookshelf.setRefreshTime(time);
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {

    }

    public static class FirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 1000);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}