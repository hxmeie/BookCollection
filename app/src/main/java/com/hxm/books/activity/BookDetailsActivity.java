package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.BookToUser;
import com.hxm.books.utils.CommonUtils;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.ToastUtils;

import org.kymjs.kjframe.KJBitmap;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by hxm on 2016/1/13.
 */
public class BookDetailsActivity extends BaseActivity {

    private TextView mBookName, mBookPrice, mBookPublisher, mBookPages, mBookAuthor, mBookPubdate;
    private ImageView mBookPic;
    private TextView mBookSummary, mBookCatalog;
    private ImageView mArrowTextDownSum, mArrowTextDownCata;
    private Button btnAddToBookshelf;
    private View mDividerLineSum, mDividerLineCata;
    private Book mBook;
    private BookToUser bookToUser;
    private int maxLineSum = 5;
    private int maxLineCata = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Bundle bundle = this.getIntent().getExtras();
        mBook = (Book) bundle.getSerializable("bookObject");
        bookToUser=new BookToUser();
        LogUtil.i(mBook.toString());
        initView();
        isBookExist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBookData(mBook);
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
        mDividerLineSum = findViewById(R.id.content_divider_line_summary);
        mBookCatalog = (TextView) findViewById(R.id.tv_book_catalog_content);
        mArrowTextDownCata = (ImageView) findViewById(R.id.im_arrow_text_down_catalog);
        mDividerLineCata = findViewById(R.id.content_divider_line_catalog);
        btnAddToBookshelf = (Button) findViewById(R.id.btn_add_to_bookshelf);

    }

    /**
     * 判断该书是否已经存在该用户书架中
     */
    private void isBookExist() {
        BmobQuery<BookToUser> bookToUserBmobQuery =new BmobQuery<>();
        bookToUserBmobQuery.getObject(this, mBook.getIsbn(), new GetListener<BookToUser>() {
            @Override
            public void onSuccess(BookToUser bookToUser) {
                btnAddToBookshelf.setText("已加入书架");
                btnAddToBookshelf.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_add_bookshelf_exist));
                btnAddToBookshelf.setTextColor(getResources().getColor(R.color.colorWhite));
                btnAddToBookshelf.setClickable(false);
            }

            @Override
            public void onFailure(int i, String s) {
                btnAddToBookshelf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

    }


    /**
     * 设置图书简介和目录的折叠效果
     */
    private void setTextContent() {
        CommonUtils.setTextAnim(mBookSummary, mArrowTextDownSum, mDividerLineSum, maxLineSum);
        CommonUtils.setTextAnim(mBookCatalog, mArrowTextDownCata, mDividerLineCata, maxLineCata);
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
        KJBitmap bookPic = new KJBitmap();
        bookPic.display(mBookPic, obj.getBookImage());
//        bookPic.displayCacheOrDefult(mBookPic,mBook.getBookImage(),R.mipmap.no_cover);
    }

}
