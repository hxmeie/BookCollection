package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hxm.books.R;

/**
 * 发现
 * Created by hxm on 2016/1/25.
 */
public class DiscoveryFragment extends Fragment implements View.OnClickListener{

    private RelativeLayout layout_search;
    private RelativeLayout layout_recommend;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery,container,false);
        layout_recommend= (RelativeLayout) view.findViewById(R.id.fg_discovery_recommend);
        layout_search= (RelativeLayout) view.findViewById(R.id.fg_discovery_search);
        layout_search.setOnClickListener(this);
        layout_recommend.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.fg_discovery_recommend:
                intent=new Intent(getActivity(),AddNewBookActivity.class);
                startActivity(intent);
                break;
            case R.id.fg_discovery_search:
                intent=new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;
        }
    }
}
