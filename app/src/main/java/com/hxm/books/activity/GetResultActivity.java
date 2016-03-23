package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;

import com.hxm.books.R;
import com.hxm.books.view.ClearEditText;
import com.hxm.books.view.HeaderLayout;

/**
 * 用于获取EditText输入信息
 * Created by hxm on 2016/3/23.
 */
public class GetResultActivity extends BaseActivity {
    private String title="设置昵称";
    private ClearEditText editText;
    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_result);
        initBothLeftAndRightBar(title, "完成", R.color.colorBase, new HeaderLayout.headerLayoutRightOnclickLister() {
            @Override
            public void onClick() {
                result = editText.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("nickname", result);
                GetResultActivity.this.setResult(0, intent);
                finish();
            }
        });
        editText= (ClearEditText) findViewById(R.id.et_get_result);

    }
}
