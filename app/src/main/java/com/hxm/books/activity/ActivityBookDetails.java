package com.hxm.books.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.Book;
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
        mBookSummary.setText(mBook.getSummary());
        mBookAuthor.setText(mBook.getAuthor());
        mBookPublisher.setText(mBook.getPublisher());
        KJBitmap bookPic=new KJBitmap();
        bookPic.display(mBookPic,mBook.getBookImage());
//        bookPic.displayCacheOrDefult(mBookPic,mBook.getBookImage(),R.mipmap.no_cover);
    }




}
