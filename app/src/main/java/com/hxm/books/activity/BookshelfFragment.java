package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hxm.books.R;

/**
 * Created by hxm on 2016/1/25.
 */
public class BookshelfFragment extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private ImageButton mScanBtn;
    private View view;
    private ListView listBookshelf;
    private SwipeRefreshLayout mRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookshelf,container,false);
        initView();
        return view;
    }

    private void initView(){
        mScanBtn = (ImageButton) view.findViewById(R.id.im_btn_scan);
        mRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.bookshelf_refresh_layout);
        listBookshelf= (ListView) view.findViewById(R.id.list_bookshelf);
        mRefreshLayout.setColorSchemeResources(R.color.colorBase);
        mScanBtn.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.im_btn_scan:
                intent=new Intent(getActivity(),ScanActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }
}
