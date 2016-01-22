package com.hxm.books.activity;

import android.os.Bundle;
import com.hxm.books.R;

/**
 * Created by hxm on 2016/1/13.
 */
public class ActivityMain extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView(){
        initOnlyTitle(stringId(this,R.string.activity_main_title));
    }
}
