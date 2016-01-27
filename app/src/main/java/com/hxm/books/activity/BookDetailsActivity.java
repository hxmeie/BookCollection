package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.listener.TextAndArrowListener;
import com.hxm.books.utils.CommonUtils;
import com.hxm.books.utils.HttpUtil;
import com.hxm.books.utils.LogUtil;
import com.loopj.android.http.TextHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJBitmap;

/**
 * Created by hxm on 2016/1/13.
 */
public class BookDetailsActivity extends BaseActivity {

    private TextView mBookName,mBookPrice,mBookPublisher,mBookPages,mBookAuthor,mBookPubdate;
    private ImageView mBookPic;
    private TextView mBookSummary,mBookCatalog;
    private ImageView mArrowTextDownSum,mArrowTextDownCata;
    private View mDividerLineSum,mDividerLineCata;
    private Book mBook;
    private int maxLineSum=5;
    private int maxLineCata=8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Bundle bundle = this.getIntent().getExtras();
        mBook= (Book) bundle.getSerializable("bookObject");
        LogUtil.i(mBook.toString());
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBookData(mBook);
    }

    //    初始化view
    private void initView(){
        initOnlyTitleAndLeftBar(stringId(this,R.string.activity_book_details_title));
        mBookAuthor= (TextView) findViewById(R.id.tv_book_author_content);
        mBookName= (TextView) findViewById(R.id.tv_book_name_content);
        mBookPages= (TextView) findViewById(R.id.tv_book_pages_content);
        mBookPrice= (TextView) findViewById(R.id.tv_book_price_content);
        mBookPublisher= (TextView) findViewById(R.id.tv_book_publisher_content);
        mBookPubdate= (TextView) findViewById(R.id.tv_book_pubdate_content);
        mBookSummary= (TextView) findViewById(R.id.tv_book_summary_content);
        mBookPic= (ImageView) findViewById(R.id.iv_book_pic);
        mArrowTextDownSum= (ImageView) findViewById(R.id.im_arrow_text_down);
        mDividerLineSum=findViewById(R.id.content_divider_line_summary);
        mBookCatalog= (TextView) findViewById(R.id.tv_book_catalog_content);
        mArrowTextDownCata= (ImageView) findViewById(R.id.im_arrow_text_down_catalog);
        mDividerLineCata=findViewById(R.id.content_divider_line_catalog);

    }

    /**
     * 设置图书简介和目录的折叠效果
     */
    private void setTextContent(){
        CommonUtils.setTextAnim(mBookSummary,mArrowTextDownSum,mDividerLineSum,maxLineSum);
        CommonUtils.setTextAnim(mBookCatalog,mArrowTextDownCata,mDividerLineCata,maxLineCata);
    }

    private void setBookData(Book obj){

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
        KJBitmap bookPic=new KJBitmap();
        bookPic.display(mBookPic, obj.getBookImage());
//        bookPic.displayCacheOrDefult(mBookPic,mBook.getBookImage(),R.mipmap.no_cover);
    }

}
