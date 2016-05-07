package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.ClearEditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;

/**
 * 如果扫描图书没有结果，则可以自己手动添加图书
 * Created by hxm on 2016/5/6.
 */
public class AddNewBookActivity extends BaseActivity {
    private ClearEditText setBook,setISBN,setAuthor,setClassify,setPages,setPrice,setPublishDate,setPublishingCompany,setBookSummary,setBookCatalog;
    private Button btnCommit;
    private LinearLayout setImage;
    private ImageView bookPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_book);
        initView();
    }

    private void initView() {
        initOnlyTitleAndLeftBar("添加新书");
        setBook= (ClearEditText) findViewById(R.id.ev_set_book_name);
        setISBN= (ClearEditText) findViewById(R.id.ev_set_isbn);
        setAuthor= (ClearEditText) findViewById(R.id.ev_set_author);
        setClassify= (ClearEditText) findViewById(R.id.ev_set_classify);
        setPages= (ClearEditText) findViewById(R.id.ev_set_pages);
        setPrice= (ClearEditText) findViewById(R.id.ev_set_price);
        setPublishDate= (ClearEditText) findViewById(R.id.ev_set_publish_date);
        setPublishingCompany= (ClearEditText) findViewById(R.id.ev_set_publishing_company);
        setBookSummary= (ClearEditText) findViewById(R.id.ev_set_summary);
        setBookCatalog= (ClearEditText) findViewById(R.id.ev_set_catalog);
        btnCommit= (Button) findViewById(R.id.btn_set_book_commit);
        setImage= (LinearLayout) findViewById(R.id.set_book_pic);
        bookPic= (ImageView) findViewById(R.id.iv_set_book_pic);

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBook();
            }
        });
    }

    private void addNewBook(){
        final Book book=new Book();
        book.setTitle(getText(setBook));
        book.setIsbn(getText(setISBN));
        book.setAuthor(getText(setAuthor));
        book.setTag1(getText(setClassify));
        book.setPages(getText(setPages));
        book.setPrice(getText(setPrice));
        book.setPubdate(getText(setPublishDate));
        book.setPublisher(getText(setPublishingCompany));
        book.setSummary(getText(setBookSummary));
        book.setCatalog(getText(setBookCatalog));
        book.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ToastUtils.show(AddNewBookActivity.this,"添加成功");
                MyUser user= BmobUser.getCurrentUser(AddNewBookActivity.this,MyUser.class);
                BmobRelation relation=new BmobRelation();
                relation.add(book);
                user.setLikes(relation);
                user.update(AddNewBookActivity.this);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private String getText(ClearEditText editText){
        return editText.getText().toString().trim();
    }
}
