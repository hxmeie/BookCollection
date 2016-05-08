package com.hxm.books.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.MyApplication;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.RegexpUtils;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.ClearEditText;
import com.hxm.books.view.HeaderLayout;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 如果扫描图书没有结果，则可以自己手动添加图书
 * Created by hxm on 2016/5/6.
 */
public class AddNewBookActivity extends BaseActivity {
    private ClearEditText setBook, setISBN, setAuthor, setClassify, setPages, setPrice, setPublishDate, setPublishingCompany, setBookSummary, setBookCatalog;
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
//        initOnlyTitleAndLeftBar("添加新书");
        initBothLeftAndRightBar("添加新书", "清空", R.color.transparent, new HeaderLayout.headerLayoutRightOnclickLister() {
            @Override
            public void onClick() {
                setEmptyText();
            }
        });
        setBook = (ClearEditText) findViewById(R.id.ev_set_book_name);
        setISBN = (ClearEditText) findViewById(R.id.ev_set_isbn);
        setAuthor = (ClearEditText) findViewById(R.id.ev_set_author);
        setClassify = (ClearEditText) findViewById(R.id.ev_set_classify);
        setPages = (ClearEditText) findViewById(R.id.ev_set_pages);
        setPrice = (ClearEditText) findViewById(R.id.ev_set_price);
        setPublishDate = (ClearEditText) findViewById(R.id.ev_set_publish_date);
        setPublishingCompany = (ClearEditText) findViewById(R.id.ev_set_publishing_company);
        setBookSummary = (ClearEditText) findViewById(R.id.ev_set_summary);
        setBookCatalog = (ClearEditText) findViewById(R.id.ev_set_catalog);
        btnCommit = (Button) findViewById(R.id.btn_set_book_commit);
        setImage = (LinearLayout) findViewById(R.id.set_book_pic);
        bookPic = (ImageView) findViewById(R.id.iv_set_book_pic);

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBook();
            }
        });

    }

    private void addNewBook() {
        if (getText(setBook).isEmpty() || getText(setISBN).isEmpty() || getText(setClassify).isEmpty()) {
            ToastUtils.show(this, "必填项不能为空！");
            return;
        }
        if (!RegexpUtils.isRegexpValidate(getText(setISBN), RegexpUtils.ISBN)) {
            ToastUtils.show(this, "ISBN号填写不正确！");
            return;
        }
        queryStarBook();
    }

    /**
     * 查询当前用户是否收藏该书
     */
    private void queryStarBook() {
        BmobQuery<Book> query = new BmobQuery<>();
        MyUser user = BmobUser.getCurrentUser(MyApplication.getInstance(), MyUser.class);
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.addQueryKeys("isbn");
        query.addWhereEqualTo("isbn", getText(setISBN));
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                LogUtil.i("list_size", list.size() + "");
                if (list.size() != 0) {
                    if (getText(setISBN).equals(list.get(0).getIsbn())) {
                        final NormalDialog dialog = new NormalDialog(AddNewBookActivity.this);
                        dialog.title("提示")
                                .content("书籍已存在书架中")
                                .showAnim(null)
                                .dismissAnim(null)
                                .btnNum(1)
                                .btnText("确定")
                                .show();
                        dialog.setOnBtnClickL(new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        });
                    }
                } else {
                    addBookToBookShelf();
                }

            }
            @Override
            public void onError(int i, String s) {
                ToastUtils.show(AddNewBookActivity.this, "失败" + s);
            }
        });
    }

    public void addBookToBookShelf() {
        final Book book = new Book();
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
        book.save(AddNewBookActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                MyUser user = BmobUser.getCurrentUser(AddNewBookActivity.this, MyUser.class);
                BmobRelation relation = new BmobRelation();
                relation.add(book);
                user.setLikes(relation);
                user.update(AddNewBookActivity.this);
                final NormalDialog dialog = new NormalDialog(AddNewBookActivity.this);
                dialog.btnText("确定")
                        .btnNum(1)
                        .title("提示")
                        .content("上传成功")
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();
                dialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.show(AddNewBookActivity.this, "失败" + s);
            }
        });
    }

    public void setEmptyText() {
        setBook.setText("");
        setISBN.setText("");
        setClassify.setText("");
        setAuthor.setText("");
        setPages.setText("");
        setPrice.setText("");
        setPublishDate.setText("");
        setPublishingCompany.setText("");
        setBookSummary.setText("");
        setBookCatalog.setText("");

    }

    private String getText(ClearEditText editText) {
        return editText.getText().toString().trim();
    }
}
