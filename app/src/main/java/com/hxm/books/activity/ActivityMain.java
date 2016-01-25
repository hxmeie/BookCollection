package com.hxm.books.activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.hxm.books.R;

/**
 * Created by hxm on 2016/1/13.
 */
public class ActivityMain extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup menuGroup;
    private RadioButton btnBookshelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView(){
        initOnlyTitle(stringId(this,R.string.title_bookshelf));
        menuGroup= (RadioGroup) findViewById(R.id.menu_radio_group);
        btnBookshelf= (RadioButton) findViewById(R.id.btn_bookshelf);
        btnBookshelf.setChecked(true);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.btn_bookshelf:


                break;
            case R.id.btn_classify:


                break;
            case R.id.btn_discovery:


                break;
            case R.id.btn_mine:


                break;
        }
    }
}
