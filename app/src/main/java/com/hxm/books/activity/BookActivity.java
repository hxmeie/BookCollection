package com.hxm.books.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.utils.CommonUtils;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hxm on 2016/2/2.
 * 书籍信息界面
 */
public class BookActivity extends BaseActivity implements View.OnClickListener{
    private TextView  mBookPrice, mBookPublisher, mBookPages, mBookAuthor, mBookPubdate;
    private ImageView mBookPic;
    private TextView mBookSummary, mBookCatalog,tvClassifyName;
    private Button btnModify;
    private EditText editText;
    private AlertDialog dialog;
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
        tvClassifyName= (TextView) findViewById(R.id.tv_book_activity_classify);
        mBookPublisher = (TextView) findViewById(R.id.tv_activity_book_publisher_content);
        btnModify= (Button) findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(this);
        mBookPubdate = (TextView) findViewById(R.id.tv_activity_book_pubdate_content);
        mBookSummary = (TextView) findViewById(R.id.tv_activity_book_summary_content);
        mBookPic = (ImageView) findViewById(R.id.iv_activity_book_pic);
        mArrowTextDownSum = (ImageView) findViewById(R.id.iv_arrow_text_down);
//        mDividerLineSum = findViewById(R.id.divider_line_summary);
        mBookCatalog = (TextView) findViewById(R.id.tv_activity_book_catalog_content);
        mArrowTextDownCata = (ImageView) findViewById(R.id.iv_arrow_text_down_catalog);
//        mDividerLineCata = findViewById(R.id.divider_line_catalog);

        setBookData(book);
        editText=new EditText(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
        builder.setView(editText);
        builder.setTitle("输入分类名");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String key=editText.getText().toString();
                Book mBook=new Book();
                mBook.setTag1(key);
                mBook.update(BookActivity.this,book.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        tvClassifyName.setText(key);
                        ToastUtils.show(BookActivity.this,"修改成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ToastUtils.show(BookActivity.this,"修改失败");
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog=builder.create();
    }

    private void setBookData(Book obj) {
        mBookPages.setText(obj.getPages());
        mBookPrice.setText(obj.getPrice());
        mBookAuthor.setText(obj.getAuthor());
        mBookPublisher.setText(obj.getPublisher());
        mBookPubdate.setText(obj.getPubdate());
        mBookSummary.setText(obj.getSummary());
        mBookCatalog.setText(obj.getCatalog());
        if (obj.getTag1().isEmpty()){
            tvClassifyName.setText("未分类");
        }else {
            tvClassifyName.setText(obj.getTag1());
        }
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

    @Override
    public void onClick(View v) {
       dialog.show();
    }
}
