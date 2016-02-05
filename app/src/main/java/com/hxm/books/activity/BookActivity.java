package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.utils.CommonUtils;
import com.hxm.books.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by hxm on 2016/2/2.
 */
public class BookActivity extends BaseActivity {
    private TextView  mBookPrice, mBookPublisher, mBookPages, mBookAuthor, mBookPubdate;
    private ImageView mBookPic;
    private TextView mBookSummary, mBookCatalog;
    private ImageView mArrowTextDownSum, mArrowTextDownCata;
//    private View mDividerLineSum, mDividerLineCata;
    private int maxLineSum = 5;
    private int maxLineCata = 8;
    private Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Bundle bundle = getIntent().getExtras();
        book= (Book) bundle.getSerializable("bookinfo");
        LogUtil.i("BookActivity",book.toString());
        initView();
    }

    //    初始化view
    private void initView() {
        initOnlyTitleAndLeftBar("《"+book.getTitle()+"》");
        mBookAuthor = (TextView) findViewById(R.id.tv_activity_book_author_content);
        mBookPages = (TextView) findViewById(R.id.tv_activity_book_pages_content);
        mBookPrice = (TextView) findViewById(R.id.tv_activity_book_price_content);
        mBookPublisher = (TextView) findViewById(R.id.tv_activity_book_publisher_content);
        mBookPubdate = (TextView) findViewById(R.id.tv_activity_book_pubdate_content);
        mBookSummary = (TextView) findViewById(R.id.tv_activity_book_summary_content);
        mBookPic = (ImageView) findViewById(R.id.iv_activity_book_pic);
        mArrowTextDownSum = (ImageView) findViewById(R.id.iv_arrow_text_down);
//        mDividerLineSum = findViewById(R.id.divider_line_summary);
        mBookCatalog = (TextView) findViewById(R.id.tv_activity_book_catalog_content);
        mArrowTextDownCata = (ImageView) findViewById(R.id.iv_arrow_text_down_catalog);
//        mDividerLineCata = findViewById(R.id.divider_line_catalog);

        setBookData(book);
    }

    private void setBookData(Book obj) {
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

    /**
     * 设置图书简介和目录的折叠效果
     */
    private void setTextContent() {
        CommonUtils.setTextAnim(mBookSummary, mArrowTextDownSum, maxLineSum);
        CommonUtils.setTextAnim(mBookCatalog, mArrowTextDownCata,  maxLineCata);
    }
}
