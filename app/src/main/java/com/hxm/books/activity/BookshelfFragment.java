package com.hxm.books.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import com.hxm.books.MyApplication;
import com.hxm.books.R;
import com.hxm.books.adapter.BookShelfAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.BookToUser;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by hxm on 2016/1/25.
 */
public class BookshelfFragment extends Fragment implements View.OnClickListener{
    private ImageButton mScanBtn;
    private View view;
    private ListView listBookshelf;
    private MyUser user = MyApplication.user;
    private List<Book> bookList;
    private List<BookToUser> bookToUserList = new ArrayList<>();
    private BookShelfAdapter mAdapter;
    private int count = 15;		// 每页的数据是10条
    private int curPage = 0;		// 当前页的编号，从0开始
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        initView();
        getBookList();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirstDisplayListener.displayedImages.clear();
    }

    private void initView() {
        mScanBtn = (ImageButton) view.findViewById(R.id.im_btn_scan);
        listBookshelf = (ListView) view.findViewById(R.id.list_bookshelf);
        mScanBtn.setOnClickListener(this);
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


    private void getBookList(){
        bookList=new ArrayList<>();
        String sql="select * from Book";
        new BmobQuery<Book>().doSQLQuery(getContext(), sql, new SQLQueryListener<Book>() {
            @Override
            public void done(BmobQueryResult<Book> bmobQueryResult, BmobException e) {
                if (e==null){
                    List<Book> books=bmobQueryResult.getResults();
                    for (int i= 0;i<books.size();i++){
                        bookList.add(books.get(i));
                        LogUtil.i("infoM",books.get(i).toString());
                    }

                }
                mAdapter=new BookShelfAdapter(getContext(),bookList);
                listBookshelf.setAdapter(mAdapter);
            }
        });
    }

    public static class FirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}