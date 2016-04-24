package com.hxm.books.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.bean.ClassifyData;
import com.hxm.books.utils.ViewHolder;

import java.util.List;

/**
 * 图书分类
 * Created by hxm on 2016/4/24.
 */
public class ClassifyAdapter extends BaseAdapter{

    private List<ClassifyData> mList;
    private Context context;

    public ClassifyAdapter() {
    }

    public ClassifyAdapter(Context context,List<ClassifyData> mList) {
        this.mList=mList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_classify,parent,false);
        }
        TextView tvCount= ViewHolder.get(convertView,R.id.tv_book_count);
        TextView tvClassifyName=ViewHolder.get(convertView,R.id.tv_classify_name);

        tvCount.setText(mList.get(position).getCount());
        tvClassifyName.setText(mList.get(position).getClassifyName());
        return convertView;
    }
}
