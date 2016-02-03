package com.hxm.books.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hxm.books.AppManager;
import com.hxm.books.MyApplication;
import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.BookToUser;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;


/**
 * Created by hxm on 2016/1/13.
 */
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup menuGroup;
    private RadioButton btnBookshelf;
    private BookshelfFragment bookshelfFragment;
    private ClassifyFragment classifyFragment;
    private DiscoveryFragment discoveryFragment;
    private MineFragment mineFragment;
    private FragmentManager mFragmentManger;
    private DoubleClickExitHelper mDoubleClickExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManger=getSupportFragmentManager();
        initView();
        AppManager.getAppManager().addActivity(this);
    }

    public void initView(){
        mDoubleClickExit = new DoubleClickExitHelper(this);
        menuGroup= (RadioGroup) findViewById(R.id.menu_radio_group);
        menuGroup.setOnCheckedChangeListener(this);
        btnBookshelf= (RadioButton) findViewById(R.id.btn_bookshelf);
        btnBookshelf.setChecked(true);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fragmentTransaction=mFragmentManger.beginTransaction();
        hideAllFragment(fragmentTransaction);
        switch (checkedId){
            case R.id.btn_bookshelf:
                LogUtil.d("点击书架");
                if(bookshelfFragment ==null){
                    bookshelfFragment =new BookshelfFragment();
                    fragmentTransaction.add(R.id.fragment_container, bookshelfFragment);
                }else {
                    fragmentTransaction.show(bookshelfFragment);
                }

                break;
            case R.id.btn_classify:
                LogUtil.d("点击分类");
                if(classifyFragment ==null){
                    classifyFragment =new ClassifyFragment();
                    fragmentTransaction.add(R.id.fragment_container, classifyFragment);
                }else {
                    fragmentTransaction.show(classifyFragment);
                }

                break;
            case R.id.btn_discovery:
                LogUtil.d("点击发现");
                if(discoveryFragment ==null){
                    discoveryFragment =new DiscoveryFragment();
                    fragmentTransaction.add(R.id.fragment_container, discoveryFragment);
                }else {
                    fragmentTransaction.show(discoveryFragment);
                }

                break;
            case R.id.btn_mine:
                LogUtil.d("点击我的");
                if(mineFragment ==null){
                    mineFragment =new MineFragment();
                    fragmentTransaction.add(R.id.fragment_container, mineFragment);
                }else {
                    fragmentTransaction.show(mineFragment);
                }

                break;
        }
        fragmentTransaction.commit();
    }

    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(bookshelfFragment !=null)
            fragmentTransaction.hide(bookshelfFragment);
        if(classifyFragment !=null)
            fragmentTransaction.hide(classifyFragment);
        if(discoveryFragment !=null)
            fragmentTransaction.hide(discoveryFragment);
        if(mineFragment !=null)
            fragmentTransaction.hide(mineFragment);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用

                return mDoubleClickExit.onKeyDown(keyCode, event);

        }
        return super.onKeyDown(keyCode, event);
    }
}
