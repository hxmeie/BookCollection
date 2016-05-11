package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.BookISBN;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.MyApplication;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.NetUtil;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.loadingindicator.AVLoadingIndicatorView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by hxm on 2016/1/13.
 * 扫描后的书籍信息界面
 */
public class ScanBookDetailsActivity extends BaseActivity {
    private String TAG="ScanBookDetailsActivity";
    private TextView mBookName, mBookPrice, mBookPublisher, mBookPages, mBookAuthor, mBookPubdate;
    private ImageView mBookPic;
    private TextView mBookSummary, mBookCatalog;
    private ImageView mArrowTextDownSum, mArrowTextDownCata;
    private Button btnAddToBookshelf;
    private AVLoadingIndicatorView loadingView;
    private int maxLineSum = 5;
    private int maxLineCata = 8;
    private String bookISBN;
    private Book mBook;
    private BookISBN isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        bookISBN=getIntent().getStringExtra("book_isbn");
        LogUtil.i(TAG,"图书ISBN号:"+bookISBN);
        initView();
        queryBook();
        queryStarBook();
    }

    //    初始化view
    private void initView() {
        initOnlyTitleAndLeftBar(stringId(this, R.string.activity_book_details_title));
        mBookAuthor = (TextView) findViewById(R.id.tv_book_author_content);
        mBookName = (TextView) findViewById(R.id.tv_book_name_content);
        mBookPages = (TextView) findViewById(R.id.tv_book_pages_content);
        mBookPrice = (TextView) findViewById(R.id.tv_book_price_content);
        mBookPublisher = (TextView) findViewById(R.id.tv_book_publisher_content);
        mBookPubdate = (TextView) findViewById(R.id.tv_book_pubdate_content);
        mBookSummary = (TextView) findViewById(R.id.tv_book_summary_content);
        mBookPic = (ImageView) findViewById(R.id.iv_book_pic);
        mArrowTextDownSum = (ImageView) findViewById(R.id.im_arrow_text_down);
        loadingView= (AVLoadingIndicatorView) findViewById(R.id.book_details_loading_view);
//        mDividerLineSum = findViewById(R.id.content_divider_line_summary);
        mBookCatalog = (TextView) findViewById(R.id.tv_book_catalog_content);
        mArrowTextDownCata = (ImageView) findViewById(R.id.im_arrow_text_down_catalog);
//        mDividerLineCata = findViewById(R.id.content_divider_line_catalog);
        btnAddToBookshelf = (Button) findViewById(R.id.btn_add_to_bookshelf);

        btnAddToBookshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStarBook();
            }
        });

    }

    /**
     * 查询当前用户是否收藏该书
     */
    private void queryStarBook() {
        BmobQuery<Book> query=new BmobQuery<>();
        MyUser user=BmobUser.getCurrentUser(MyApplication.getInstance(), MyUser.class);
        query.addWhereRelatedTo("likes",new BmobPointer(user));
        query.addQueryKeys("isbn");
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                LogUtil.i(TAG, "query_result_from_bookshelf list size is:" + list.size());
                for (int i = 0; i < list.size(); i++) {
                    if (bookISBN.equals(list.get(i).getIsbn())) {
                        setBtnState();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        loadingView.setVisibility(View.GONE);
    }

    /**
     * 查询图书信息
     */
    private void queryBook(){
        loadingView.setVisibility(View.VISIBLE);
        BmobQuery<Book> query=new BmobQuery<>();
        query.addWhereEqualTo("isbn", bookISBN);
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                mBook = list.get(0);
                setBookData(mBook);
            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.show(ScanBookDetailsActivity.this, "获取图书信息失败");
                LogUtil.i(TAG, s);
            }
        });
        BmobQuery<BookISBN> isbnQuery=new BmobQuery<>();
        isbnQuery.addWhereEqualTo("bookISBN",bookISBN);
        isbnQuery.findObjects(this, new FindListener<BookISBN>() {
            @Override
            public void onSuccess(List<BookISBN> list) {
                isbn=list.get(0);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 添加收藏
     */
    private void addStarBook() {
        MyUser user= BmobUser.getCurrentUser(MyApplication.getInstance(),MyUser.class);
        BmobRelation bookRelation=new BmobRelation();
        bookRelation.add(mBook);
//        BmobRelation isbnRelation=new BmobRelation();
//        isbnRelation.add(isbn);
        user.setLikes(bookRelation);
//        user.setLikesISBN(isbnRelation);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG,"多对多关系添加成功");
                setBtnState();
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG,"多对多关系添加失败"+s);
            }
        });
    }


    /**
     * 设置图书简介和目录的折叠效果
     */
    private void setTextContent() {
        NetUtil.setTextAnim(mBookSummary, mArrowTextDownSum, maxLineSum);
        NetUtil.setTextAnim(mBookCatalog, mArrowTextDownCata, maxLineCata);
    }

    private void setBookData(Book obj) {

        mBookName.setText(obj.getTitle());
        LogUtil.i(mBookName.getText().toString());
        mBookPages.setText(obj.getPages());
        mBookPrice.setText(obj.getPrice());
        mBookAuthor.setText(obj.getAuthor());
        mBookPublisher.setText(obj.getPublisher());
        mBookPubdate.setText(obj.getPubdate());
        mBookSummary.setText(obj.getSummary());
        mBookCatalog.setText(obj.getCatalog());
        setTextContent();
        ImageLoader.getInstance().displayImage(obj.getBookImage(), mBookPic);
    }

    private void setBtnState() {
        btnAddToBookshelf.setText("已加入书架");
        btnAddToBookshelf.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_add_bookshelf_exist));
        btnAddToBookshelf.setTextColor(getResources().getColor(R.color.colorWhite));
        btnAddToBookshelf.setClickable(false);
    }
}
