package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hxm.books.config.MyApplication;
import com.hxm.books.R;
import com.hxm.books.adapter.BookAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.listener.DiakogTwoBtnEvent;
import com.hxm.books.listener.FirstDisplayListener;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.utils.cache.FileCache;
import com.hxm.books.view.EmptyView;
import com.hxm.books.view.MyDialog;
import com.hxm.books.view.RefreshLayout;
import com.hxm.books.view.loadingindicator.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 书架
 * Created by hxm on 2016/1/25.
 */
public class BookshelfFragment extends Fragment implements View.OnClickListener, RefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    private ImageButton mScanBtn;
    private View view;
    private ListView listview_book;
    private List<Book> bookList = new ArrayList<>();
    private BookAdapter mAdapter;
    private FileCache mCache;
    private AVLoadingIndicatorView loading;
    private RefreshLayout refreshLayout;
    private EmptyView emptyView;
    private int pageLimit = 10;
    private int lastPageNum = 0;
    private boolean firstLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = MyApplication.cache;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        initView();
        setListViewData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirstDisplayListener.displayedImages.clear();
    }

    private void initView() {
        mScanBtn = (ImageButton) view.findViewById(R.id.im_btn_scan);
        loading = (AVLoadingIndicatorView) view.findViewById(R.id.loading_view);
        listview_book = (ListView) view.findViewById(R.id.listview_bookshelf);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refresh_layout);
        emptyView = (EmptyView) view.findViewById(R.id.empty_view);
        emptyView.setEmptyViewListener(new EmptyView.onEmptyViewClickListener() {
            @Override
            public void onLayoutClick() {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivity(intent);
            }
        });
        mScanBtn.setOnClickListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorBase, R.color.colorAccent, R.color.colorPrimary, R.color.colorBrowm);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadingListener(this);
        initListView();

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
    private void initListView() {

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
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                MyDialog dialog = new MyDialog(getContext());
                dialog.DialogWithTwoBtn("是否从书架中删除?", "提示", new DiakogTwoBtnEvent() {
                    @Override
                    public void leftOnClick() {
                        ToastUtils.show(getContext(), "点击左边");
                    }

                    @Override
                    public void rightOnClick() {
                        MyUser user= BmobUser.getCurrentUser(MyApplication.getInstance(),MyUser.class);
                        BmobRelation bookRelation=new BmobRelation();
                        bookRelation.remove(bookList.get(position));
                        user.setLikes(bookRelation);
                        user.update(getActivity(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.show(getActivity(),"删除成功");
                                bookList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                if (bookList.size()==0){
                                    listview_book.setEmptyView(emptyView);
                                }
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                ToastUtils.show(getActivity(),"删除失败"+s);
                            }
                        });
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
        if (firstLoad) {
            loading.setVisibility(View.VISIBLE);
        }
        BmobQuery<Book> queryBookFromStar = new BmobQuery<>();
        queryBookFromStar.setLimit(pageLimit);
        queryBookFromStar.setSkip(lastPageNum);
        MyUser user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        queryBookFromStar.addWhereRelatedTo("likes", new BmobPointer(user));
        queryBookFromStar.addQueryKeys("title,bookImage,author,summary,tag1,tag2");
        queryBookFromStar.findObjects(getActivity(), new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                for (int i = 0; i < list.size(); i++) {
                    bookList.add(list.get(i));
                }
                if (bookList.size() != 0) {
                    if (mAdapter == null) {
                        mAdapter = new BookAdapter(getActivity(), bookList);
                        listview_book.setAdapter(mAdapter);
                        loading.setVisibility(View.GONE);
                        firstLoad = false;
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    firstLoad=false;
                    loading.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    listview_book.setEmptyView(emptyView);
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
        lastPageNum += pageLimit;
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setLoading(false);
                getBooks();
            }
        }, 100);
    }

    @Override
    public void onRefresh() {
        lastPageNum = 0;
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                bookList.clear();
                getBooks();
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

}