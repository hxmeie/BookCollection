package com.hxm.books.activity;

import android.os.Bundle;

import com.hxm.books.R;
import com.hxm.books.view.HeaderLayout;

/**
 * 搜索图书
 * Created by hxm on 2016/4/21.
 */
public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initMiddleSearchView("搜索", R.color.colorBase, new HeaderLayout.headerLayoutRightOnclickLister() {
            @Override
            public void onClick() {

            }
        });
    }
}
