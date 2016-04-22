package com.hxm.books.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import com.hxm.books.R;
import com.hxm.books.config.Constants;
import com.hxm.books.utils.HttpUtil;
import com.hxm.books.view.HeaderLayout;
import com.hxm.books.view.RefreshLayout;

/**
 * 搜索图书
 * Created by hxm on 2016/4/21.
 */
public class SearchActivity extends BaseActivity implements RefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener{
    private ListView lvSearch;
    private RefreshLayout refreshLayout;
    private EditText editText;
    private int mPageSize = 20;
    private int mCurrentPageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        editText=new HeaderLayout(this).getSearchEditText();
        lvSearch= (ListView) findViewById(R.id.listview_search_result);
        refreshLayout= (RefreshLayout) findViewById(R.id.search_result_refreshlayout);
        initMiddleSearchView("搜索", R.color.colorBase, new HeaderLayout.headerLayoutRightOnclickLister() {
            @Override
            public void onClick() {

            }
        });
    }

    public void getSearchResult(){
        String keyWord=editText.getText().toString().trim();
        String url= Constants.SearchBookApi+ "?q=" + keyWord + "&start=" + (mCurrentPageIndex - 1) * mPageSize +
                "&count=" + mPageSize;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onRefresh() {

    }
}
