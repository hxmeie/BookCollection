package com.hxm.books.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.utils.HttpUtil;
import com.hxm.books.utils.LogUtil;
import com.loopj.android.http.TextHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hxm on 2016/1/13.
 */
public class ActivityBookDetails extends BaseActivity {

    private TextView mBookName,mBookPrice,mBookPublisher,mBookPages,mBookAuthor;
    private ImageView mBookPic;
    private TextView mBookSummary;
    private ImageView mArrowTextDown;
    private String bookISBN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Bundle bundle = this.getIntent().getExtras();
        bookISBN=bundle.getString("result");
        LogUtil.i("ISBN号",bookISBN);
        initView();
        getBookInfo();
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
        mArrowTextDown= (ImageView) findViewById(R.id.im_arrow_text_down);

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
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                LogUtil.i(s);

            }

        });

    }

    private void setBookData(String bookInfo){
        try {
            JSONObject jsonObject =new JSONObject(bookInfo);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
