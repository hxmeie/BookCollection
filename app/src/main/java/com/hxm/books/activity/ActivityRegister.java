package com.hxm.books.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.hxm.books.R;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.CommonUtils;
import com.hxm.books.utils.KeyBoardUtils;
import com.hxm.books.utils.MD5Util;
import com.hxm.books.utils.RegexpUtils;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.ClearEditText;

/**
 * Created by hxm on 2016/1/9.
 */
public class ActivityRegister extends BaseActivity implements View.OnClickListener{
    private ClearEditText etRegisterAccount,etPasswordFirst,etPasswordSecond;
    private Button btnRegiserCommit;
    LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        initOnlyTitleAndLeftBar(stringId(this,R.string.register_title));
        mLayout= (LinearLayout) findViewById(R.id.layout_register);
        etRegisterAccount= (ClearEditText) findViewById(R.id.et_register_account);
        etPasswordFirst= (ClearEditText) findViewById(R.id.et_register_password_first);
        etPasswordSecond= (ClearEditText) findViewById(R.id.et_register_password_second);
        btnRegiserCommit= (Button) findViewById(R.id.btn_register_commit);

        btnRegiserCommit.setOnClickListener(this);
        mLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register_commit:
                    signUp();
                break;
        }
    }

    /**
     * 注册功能
     */
    public void signUp(){
        String account=etRegisterAccount.getText().toString().trim();
        String password=etPasswordFirst.getText().toString().trim();
        String pwd_Again=etPasswordSecond.getText().toString().trim();

        if(TextUtils.isEmpty(account)){
            ToastUtils.show(this,R.string.toast_username_is_null, Toast.LENGTH_SHORT);
            return;//return 表示从被调函数返回到主调函数继续执行
        }
        if(TextUtils.isEmpty(password)){
            ToastUtils.show(this,R.string.toast_password_is_null, Toast.LENGTH_SHORT);
            return;
        }else {
            if(!RegexpUtils.isRegexpValidate(password,RegexpUtils.LETTER_NUMBER_REGEXP_6_12)){
                ToastUtils.show(this,R.string.toast_pwd_style_wrong, Toast.LENGTH_SHORT);
                return;
            }
        }
        if(TextUtils.isEmpty(pwd_Again)){
            ToastUtils.show(this,R.string.toast_pwd_again_is_null, Toast.LENGTH_SHORT);
            return;
        }
        if (!pwd_Again.equals(password)){
            ToastUtils.show(this, R.string.toast_pwd_again_is_wrong, Toast.LENGTH_LONG);
            etPasswordSecond.setText("");
            return;
        }

        //检查网络是否可用
        boolean isNetConnected= CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ToastUtils.show(this,R.string.toast_net_wrong,Toast.LENGTH_SHORT);
            return;
        }
        //进度
        final ProgressDialog mDialog=new ProgressDialog(this);
        mDialog.setMessage(stringId(this,R.string.progress_dialog_text));
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();

        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
//        final MyUser user =new MyUser();
//        user.setUsername(account);
//        user.setPassword(MD5Util.getMD5String(pwd_Again));
    }
}
