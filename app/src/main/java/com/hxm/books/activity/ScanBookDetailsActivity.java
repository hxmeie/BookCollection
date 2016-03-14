package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.MyApplication;
import com.hxm.books.utils.CommonUtils;
import com.hxm.books.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
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

    private TextView mBookName, mBookPrice, mBookPublisher, mBookPages, mBookAuthor, mBookPubdate;
    private ImageView mBookPic;
    private TextView mBookSummary, mBookCatalog;
    private ImageView mArrowTextDownSum, mArrowTextDownCata;
    private Button btnAddToBookshelf;
//    private View mDividerLineSum, mDividerLineCata;
    private Book mBook;
    private int maxLineSum = 5;
    private int maxLineCata = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Bundle bundle = this.getIntent().getExtras();
        mBook = (Book) bundle.getSerializable("bookObject");
        LogUtil.i(mBook.toString());
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBookData(mBook);
       // queryStarBook(mBook);
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
//        mDividerLineSum = findViewById(R.id.content_divider_line_summary);
        mBookCatalog = (TextView) findViewById(R.id.tv_book_catalog_content);
        mArrowTextDownCata = (ImageView) findViewById(R.id.im_arrow_text_down_catalog);
//        mDividerLineCata = findViewById(R.id.content_divider_line_catalog);
        btnAddToBookshelf = (Button) findViewById(R.id.btn_add_to_bookshelf);

        btnAddToBookshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStarBook(mBook);
            }
        });

    }

    /**
     * 查询当前用户是否收藏该书
     */
    private void queryStarBook(final Book book) {
        //查询当前用户收藏的所有图书,因此查询book表
        BmobQuery<Book> query = new BmobQuery<>();
        MyUser user= new MyUser();
        user.setObjectId(MyApplication.user.getObjectId());
        //starBooks是MyUser表中的字段，用来存储用户收藏的所有图书
        query.addWhereRelatedTo("starBooks",new BmobPointer(user));
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size()!=0){
                    for (int i=0;i<list.size();i++){
                        if (book.getIsbn().equals(list.get(i).getIsbn())){
                            setBtnState();
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                NormalDialog dialog =new NormalDialog(ScanBookDetailsActivity.this);
                dialog.style(NormalDialog.STYLE_ONE)
                        .showAnim(null)
                        .dismissAnim(null)
                        .content("查询失败！")
                        .btnText("确定")
                        .show();
                dialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        finish();
                    }
                });
            }
        });
    }
    /**
     * 添加收藏
     */
    private void addStarBook(Book book) {
        MyUser user = new MyUser();
        user.setObjectId(MyApplication.user.getObjectId());
        BmobRelation relation = new BmobRelation();
        relation.add(book);
        user.setStarBooks(relation);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i("relation", "多对多关系添加成功");
                setBtnState();
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i("relation", "多对多关系添加失败" + s);
            }
        });

    }


    /**
     * 设置图书简介和目录的折叠效果
     */
    private void setTextContent() {
        CommonUtils.setTextAnim(mBookSummary, mArrowTextDownSum, maxLineSum);
        CommonUtils.setTextAnim(mBookCatalog, mArrowTextDownCata, maxLineCata);
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
