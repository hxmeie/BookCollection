package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hxm.books.R;

/**
 * Created by hxm on 2016/1/25.
 */
public class BookshelfFragment extends Fragment implements View.OnClickListener{
    private ImageButton mScanBtn;
    private View view;

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
        mScanBtn.setOnClickListener(this);
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
}
