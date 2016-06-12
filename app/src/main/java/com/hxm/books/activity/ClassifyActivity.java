package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hxm.books.R;
import com.hxm.books.adapter.BookAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.MyApplication;
import com.hxm.books.view.loadingindicator.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * 某一类书籍
 * Created by hxm on 2016/4/25.
 */
public class ClassifyActivity extends BaseActivity {

    private AVLoadingIndicatorView loadingView;
    private ListView listview;
    private BookAdapter adapter;
    private List<Book> mList;
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        tag=getIntent().getStringExtra("tag");
        mList=new ArrayList<>();
        initView();
        getDataFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.isRefresh){
            mList.clear();
            getDataFromServer();
        }
    }

    private void initView() {

        initOnlyTitleAndLeftBar(tag);
        loadingView= (AVLoadingIndicatorView) findViewById(R.id.classify_activity_loading_view);
        listview= (ListView) findViewById(R.id.lv_classify_activity);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.isRefresh=false;
                Intent intent=new Intent(ClassifyActivity.this,BookActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("bookinfo",mList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void getDataFromServer(){
//        if(tag=="未分类"){
//            tag="";
//        }
        loadingView.setVisibility(View.VISIBLE);
        MyUser user= BmobUser.getCurrentUser(this,MyUser.class);
        BmobQuery<Book> query=new BmobQuery<>();
        query.addWhereRelatedTo("likes",new BmobPointer(user));
        query.order("-updatedAt");//按更新时间降序查询
        query.addWhereEqualTo("tag1",tag);
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                for (int i=0;i<list.size();i++){
                    mList.add(list.get(i));
                }
                loadingView.setVisibility(View.GONE);
                if (adapter!=null){
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new BookAdapter(ClassifyActivity.this, mList);
                    listview.setAdapter(adapter);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
