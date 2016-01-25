package com.hxm.books.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.hxm.books.R;


/**
 * Created by hxm on 2016/1/13.
 */
public class ActivityMain extends FragmentActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup menuGroup;
    private RadioButton btnBookshelf;
    private FragmentBookshelf fragmentBookshelf;
    private FragmentClassify fragmentClassify;
    private FragmentDiscovery fragmentDiscovery;
    private FragmentMine fragmentMine;
    private FragmentManager mFragmentManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManger=getSupportFragmentManager();
        initView();
    }

    public void initView(){
        menuGroup= (RadioGroup) findViewById(R.id.menu_radio_group);
        btnBookshelf= (RadioButton) findViewById(R.id.btn_bookshelf);
        btnBookshelf.setChecked(true);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fragmentTransaction=mFragmentManger.beginTransaction();
        hideAllFragment(fragmentTransaction);
        switch (checkedId){
            case R.id.btn_bookshelf:
                if(fragmentBookshelf==null){
                    fragmentBookshelf=new FragmentBookshelf();
                    fragmentTransaction.add(R.id.fragment_container,fragmentBookshelf);
                }else {
                    fragmentTransaction.show(fragmentBookshelf);
                }

                break;
            case R.id.btn_classify:
                if(fragmentClassify==null){
                    fragmentClassify=new FragmentClassify();
                    fragmentTransaction.add(R.id.fragment_container,fragmentClassify);
                }else {
                    fragmentTransaction.show(fragmentClassify);
                }

                break;
            case R.id.btn_discovery:
                if(fragmentDiscovery==null){
                    fragmentDiscovery=new FragmentDiscovery();
                    fragmentTransaction.add(R.id.fragment_container,fragmentDiscovery);
                }else {
                    fragmentTransaction.show(fragmentDiscovery);
                }

                break;
            case R.id.btn_mine:
                if(fragmentMine==null){
                    fragmentMine=new FragmentMine();
                    fragmentTransaction.add(R.id.fragment_container,fragmentMine);
                }else {
                    fragmentTransaction.show(fragmentMine);
                }

                break;
        }
        fragmentTransaction.commit();
    }

    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fragmentBookshelf!=null)
            fragmentTransaction.hide(fragmentBookshelf);
        if(fragmentClassify!=null)
            fragmentTransaction.hide(fragmentClassify);
        if(fragmentDiscovery!=null)
            fragmentTransaction.hide(fragmentDiscovery);
        if(fragmentMine!=null)
            fragmentTransaction.hide(fragmentMine);
    }
}
