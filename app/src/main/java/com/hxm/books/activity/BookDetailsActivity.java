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

    private TextView mBookName,mBookPrice,mBookPublisher,mBookPages,mBookAuthor;
    private ImageView mBookPic;
    private TextView mBookSummary,mBookCatalog;
    private ImageView mArrowTextDownSum,mArrowTextDownCata;
    private View mDividerLineSum,mDividerLineCata;
    private String bookISBN;
    private int maxLineSum=5;
    private int macLineCata=8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Bundle bundle = this.getIntent().getExtras();
        bookISBN=bundle.getString("result");
        LogUtil.i("ISBN号", bookISBN);
        initView();
        getBookInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //    初始化view
    private void initView(){
        initOnlyTitleAndLeftBar(stringId(this,R.string.activity_book_details_title));
        mBookAuthor= (TextView) findViewById(R.id.tv_book_author_content);
        mBookName= (TextView) findViewById(R.id.tv_book_name_content);
        mBookPages= (TextView) findViewById(R.id.tv_book_pages_content);
        mBookPrice= (TextView) findViewById(R.id.tv_book_price_content);
        mBookPublisher= (TextView) findViewById(R.id.tv_book_publisher_content);
        mBookSummary= (TextView) findViewById(R.id.tv_book_summary_content);
        mBookPic= (ImageView) findViewById(R.id.iv_book_pic);
        mArrowTextDownSum= (ImageView) findViewById(R.id.im_arrow_text_down);
        mDividerLineSum=findViewById(R.id.content_divider_line_summary);
        mBookCatalog= (TextView) findViewById(R.id.tv_book_catalog_content);
        mArrowTextDownCata= (ImageView) findViewById(R.id.im_arrow_text_down_catalog);
        mDividerLineCata=findViewById(R.id.content_divider_line_catalog);

    }

    private void setSummary(){
        //设置默认显示高度
        mBookSummary.setHeight(mBookSummary.getLineHeight()*maxLineSum);
        //根据高度来控制是否展示翻转icon
        mBookSummary.post(new Runnable() {
            @Override
            public void run() {
                mArrowTextDownSum.setVisibility(mBookSummary.getLineCount()>maxLineSum? View.VISIBLE:View.GONE);
                mDividerLineSum.setVisibility(mBookSummary.getLineCount()>maxLineSum? View.VISIBLE:View.GONE);
            }
        });
        mBookSummary.setOnClickListener(new TextAndArrowListener(mBookSummary,mArrowTextDownSum,maxLineSum));
    }

    private void setCatalog(){
        //设置默认显示高度
        mBookCatalog.setHeight(mBookCatalog.getLineHeight()*macLineCata);
        //根据高度来控制是否展示翻转icon
        mBookCatalog.post(new Runnable() {
            @Override
            public void run() {
                mArrowTextDownCata.setVisibility(mBookCatalog.getLineCount()>macLineCata? View.VISIBLE:View.GONE);
                mDividerLineCata.setVisibility(mBookCatalog.getLineCount()>macLineCata? View.VISIBLE:View.GONE);
            }
        });
        mBookCatalog.setOnClickListener(new TextAndArrowListener(mBookCatalog,mArrowTextDownCata,macLineCata));
    }


    /**
     * 根据ISBN获取图书信息
     */
    private void getBookInfo(){
        String url="https://api.douban.com/v2/book/isbn/:"+bookISBN;
        HttpUtil.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtil.e("获取失败");
                LogUtil.e(s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                LogUtil.i(s);
                setBookData(s);

            }


        });

    }

    private void setBookData(String bookInfo){
        Book mBook= new Book();
        String author="";
        try {
            JSONObject jsonObject =new JSONObject(bookInfo);
            JSONArray jsonArray=jsonObject.getJSONArray("author");
            mBook.setPages(jsonObject.getString("pages"));
            mBook.setTitle(jsonObject.getString("title"));
            mBook.setPrice(jsonObject.getString("price"));
            mBook.setSummary(jsonObject.getString("summary"));
            mBook.setPublisher(jsonObject.getString("publisher"));
            mBook.setBookImage(jsonObject.optJSONObject("images").optString("large"));
            mBook.setCatalog(jsonObject.getString("catalog"));
            for (int index=0;index<jsonArray.length();index++){
                author += jsonArray.optString(index)+" ";
            }
            mBook.setAuthor(author);

            LogUtil.i(mBook.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBookName.setText(mBook.getTitle());
        mBookPages.setText(mBook.getPages());
        mBookPrice.setText(mBook.getPrice());
        mBookAuthor.setText(mBook.getAuthor());
        mBookPublisher.setText(mBook.getPublisher());
        mBookSummary.setText(mBook.getSummary());
        setSummary();
        mBookCatalog.setText(mBook.getCatalog());
        setCatalog();
        KJBitmap bookPic=new KJBitmap();
        bookPic.display(mBookPic, mBook.getBookImage());
//        bookPic.displayCacheOrDefult(mBookPic,mBook.getBookImage(),R.mipmap.no_cover);
    }

}
