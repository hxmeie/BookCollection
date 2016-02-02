package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import com.hxm.books.MyApplication;
import com.hxm.books.R;
import com.hxm.books.adapter.BookShelfAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.BookToUser;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;
import java.util.ArrayList;
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

//    /**
//     * 分页获取数据
//     * @param page	页码
//     * @param actionType	ListView的操作类型（下拉刷新、上拉加载更多）
//     */
//    private void getUserBookInfo(int page, final int actionType) {
//
//        BmobQuery<BookToUser> bookToUserQuery = new BmobQuery<>();
//        bookToUserQuery.order("-createdAt");//按时间降序查询
//        if (actionType==STATE_MORE){
//            bookToUserQuery.addWhereEqualTo("userObjectId", user.getObjectId());
//            // 跳过之前页数并去掉重复数据
//            bookToUserQuery.setSkip(page * count+1);
//        }else{
//            page=0;
//            bookToUserQuery.setSkip(page);
//        }
//        // 设置每页数据个数
//        bookToUserQuery.setLimit(count);
//        bookToUserQuery.findObjects(getContext(), new FindListener<BookToUser>() {
//            @Override
//            public void onSuccess(List<BookToUser> list) {
//               if (list.size()>0){
//                   if(actionType == STATE_REFRESH){
//                       // 当是下拉刷新操作时，将当前页的编号重置为0，并把xxlist清空，重新添加
//                       curPage = 0;
//
//
//                   }
//
//                   // 将本次查询的数据添加到你的list中
//
//                   // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
//                   curPage++;
//
//               }
//                mRefreshLayout.onHeaderRefreshFinish();
//                mRefreshLayout.onFooterLoadFinish();
//            }
//
//            @Override
//            public void onError(int i, String s) {
//
//            }
//        });
//
//    }


    private void loadMore(){
        final BmobQuery<BookToUser> query=new BmobQuery<>();
//        query.setLimit(count);
//        query.order("-createdAt");
//        query.setSkip(curPage * count + 1);
        query.addWhereEqualTo("userObjectId", user.getObjectId());
        query.findObjects(getContext(), new FindListener<BookToUser>() {
            @Override
            public void onSuccess(List<BookToUser> list) {
                //将本次查询的数据放到bookToUserList中
                for (BookToUser btu:list){
                    bookToUserList.add(btu);
                }
//                curPage++;
                for (int i=0;i<bookToUserList.size();i++){
                    BookToUser mBookUser=bookToUserList.get(i);
                    //查询图书详细信息
                    BmobQuery<Book> bookQuery=new BmobQuery<Book>();
                    bookQuery.addWhereEqualTo("isbn",mBookUser.getIsbn());
                    bookQuery.findObjects(getContext(), new FindListener<Book>() {
                        @Override
                        public void onSuccess(List<Book> list) {
                            for (Book book:list){
                                bookList.add(book);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


}