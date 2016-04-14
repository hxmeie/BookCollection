package com.hxm.books.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.hxm.books.R;

/**
 * ListView 为空的时候加载
 * Created by hxm on 2016/4/12.
 */
public class EmptyView extends LinearLayout implements View.OnClickListener{

    private LinearLayout btn_add_book;
    private Context context;
    private onEmptyViewClickListener listener;

    public EmptyView(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    private void initView() {
        View view =View.inflate(context, R.layout.empty_listview,null);
        btn_add_book= (LinearLayout) view.findViewById(R.id.bookshelf_add_books);
        btn_add_book.setOnClickListener(this);
        setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bookshelf_add_books:
                listener.onLayoutClick();
                break;
        }
    }

    public interface onEmptyViewClickListener{
        void onLayoutClick();
    }
    public void setEmptyViewListener(onEmptyViewClickListener listener){
        this.listener=listener;
    }
}
