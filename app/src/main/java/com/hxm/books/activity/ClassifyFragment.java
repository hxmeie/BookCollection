package com.hxm.books.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hxm.books.R;
import com.hxm.books.adapter.ClassifyAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.ClassifyData;
import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.FindStatisticsListener;

/**
 * 分类
 * Created by hxm on 2016/1/25.
 */
public class ClassifyFragment extends Fragment {

    private ListView listView;
    private List<ClassifyData> mList;
    private ClassifyAdapter adapter;
    private String classify[] = {"小说",
            "文学艺术",
            "动漫/幽默",
            "娱乐时尚",
            "地图/地理/旅游",
            "生活",
            "育儿/成长",
            "保健/心理健康",
            "体育",
            "经济管理",
            "法律/政治",
            "哲学宗教",
            "社会科学",
            "古籍",
            "教育",
            "未分类",
            "计算机/网络/编程",
            "医学",
            "科学技术"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
        getDataFromServer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify, container, false);
        listView = (ListView) view.findViewById(R.id.lv_classify);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter=new ClassifyAdapter(getActivity(),mList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getDataFromServer() {
        MyUser user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.addQueryKeys("tag1");
        query.groupby(new String[]{"tag1"});
        query.setHasGroupCount(true);
        query.findStatistics(getActivity(), Book.class, new FindStatisticsListener() {
            @Override
            public void onSuccess(Object o) {
                JSONArray jsonArray = (JSONArray) o;
                if (jsonArray != null) {
                    try {
                        for (int i = 1; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String classifyName = jsonObject.getString("tag1");
                            getClassifyNum(classifyName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void getClassifyNum(final String tag){
        MyUser user = BmobUser.getCurrentUser(getActivity(), MyUser.class);
        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.addQueryKeys("tag1");
        query.addWhereEqualTo("tag1",tag);
        query.count(getActivity(), Book.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                ClassifyData data=new ClassifyData();
                data.setClassifyName(tag);
                data.setCount(String.valueOf(i));
                LogUtil.i("ClassifyFragment",tag+i);
                mList.add(data);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
}
