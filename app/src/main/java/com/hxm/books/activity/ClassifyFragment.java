package com.hxm.books.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.hxm.books.R;
import com.hxm.books.adapter.ClassifyAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.ClassifyData;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.MyApplication;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.RefreshLayout;
import com.hxm.books.view.loadingindicator.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.FindStatisticsListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 分类
 * Created by hxm on 2016/1/25.
 */
public class ClassifyFragment extends Fragment implements RefreshLayout.OnRefreshListener {

    private ListView listView;
    private List<ClassifyData> mList;
    private List<Book> mBookList;
    private ClassifyAdapter adapter;
    private AVLoadingIndicatorView loadingView;
    private RefreshLayout mRefreshLayout;
    private EditText editText;
    private AlertDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.isRefresh){
            mList.clear();
            getDataFromServer();
            MyApplication.isRefresh=false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify, container, false);
        listView = (ListView) view.findViewById(R.id.lv_classify);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.classify_refresh_layout);
        loadingView = (AVLoadingIndicatorView) view.findViewById(R.id.classify_loading_view);
        mRefreshLayout.setColorSchemeResources(R.color.colorBase, R.color.colorAccent, R.color.colorPrimary, R.color.colorBrowm);
        mRefreshLayout.setOnRefreshListener(this);
        getDataFromServer();
        initListView();
        return view;
    }

    private void initListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tag=mList.get(position).getClassifyName();
                if (tag.equals("未分类")) {
                    tag = "";
                }
                Intent intent=new Intent(getActivity(),ClassifyActivity.class);
                intent.putExtra("tag",tag);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.i("onItemLongClick", "true");
                mBookList = new ArrayList<Book>();
                String tag = mList.get(position).getClassifyName();
                if (tag.equals("未分类")) {
                    tag = "";
                }
                BmobQuery<Book> query = new BmobQuery<Book>();
                MyUser user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
                query.addWhereRelatedTo("likes", new BmobPointer(user));
                query.addWhereEqualTo("tag1", tag);
                query.addQueryKeys("objectId");
                query.findObjects(getActivity(), new FindListener<Book>() {
                    @Override
                    public void onSuccess(List<Book> list) {
                        for (int i = 0; i < list.size(); i++) {
                            mBookList.add(list.get(i));
                            LogUtil.i("mBookList", list.get(i).getObjectId());
                        }
                        inputDialog("重命名", mBookList);
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                return true;
            }
        });
    }

    public void inputDialog(String title, final List<Book> list) {
        editText = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(title);
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtil.i("objectId", editText.getText().toString());
                List<BmobObject> books = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    Book book = new Book();
                    book.setTag1(editText.getText().toString());
                    book.setObjectId(list.get(i).getObjectId());
                    books.add(book);
                }
                new BmobObject().updateBatch(getActivity(), books, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ToastUtils.show(getActivity(), "重命名成功");
                        mList.clear();
                        getDataFromServer();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }
    private void getDataFromServer() {
        loadingView.setVisibility(View.VISIBLE);
        MyUser user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(user));
//        query.addQueryKeys("tag1");//添加此代码会让查询的返回值里没有“_count”字段
        query.groupby(new String[]{"tag1"});
        query.setHasGroupCount(true);
        query.findStatistics(getActivity(), Book.class, new FindStatisticsListener() {
            @Override
            public void onSuccess(Object o) {
                JSONArray jsonArray = (JSONArray) o;
                LogUtil.i("Class",jsonArray.toString());
                ClassifyData data = null;
                if (jsonArray != null) {
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String classifyName = jsonObject.getString("tag1");
                            if(classifyName.isEmpty()){
                                classifyName="未分类";
                            }
                            int count = jsonObject.getInt("_count");
                            data = new ClassifyData();
                            data.setCount(String.valueOf(count));
                            data.setClassifyName(classifyName);
                            mList.add(data);
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        adapter = new ClassifyAdapter(getActivity(), mList);
                        listView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                getDataFromServer();
                mRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
