package com.hxm.books.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hxm.books.R;
import com.hxm.books.adapter.BookAdapter;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.Constants;
import com.hxm.books.utils.HttpUtil;
import com.hxm.books.utils.KeyBoardUtils;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.ClearEditText;
import com.hxm.books.view.HeaderLayout;
import com.hxm.books.view.RefreshLayout;
import com.hxm.books.view.loadingindicator.AVLoadingIndicatorView;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * 搜索图书
 * Created by hxm on 2016/4/21.
 */
public class SearchActivity extends BaseActivity implements RefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener{
    private ListView lvSearch;
    private RefreshLayout refreshLayout;
    private ClearEditText editText;
    private TextView tvEmpty;
    private Book mBook;
    private BookAdapter adapter;
    private AVLoadingIndicatorView loadingVIew;
    private int mPageSize = 10;
    private int mCurrentPageIndex = 1;
    private int search_tag=1;
    private ArrayList<Book> bookList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        lvSearch= (ListView) findViewById(R.id.listview_search_result);
        refreshLayout= (RefreshLayout) findViewById(R.id.search_result_refreshlayout);
        loadingVIew= (AVLoadingIndicatorView) findViewById(R.id.search_loading_view);
        tvEmpty= (TextView) findViewById(R.id.tv_empty);
        initMiddleSearchView("搜索", R.color.colorBase, new HeaderLayout.headerLayoutRightOnclickLister() {
            @Override
            public void onClick() {
                editText=mHeaderLayout.getSearchEditText();
                String keyWord=editText.getText().toString();
                KeyBoardUtils.closeKeybord(editText,SearchActivity.this);
                ToastUtils.show(SearchActivity.this,keyWord);
                switch (search_tag){
                    case 1:
//                        getSearchResult();
                        getLocalBooks(keyWord);
                        search_tag=2;
                        break;
                    case 2:
                        bookList.clear();
//                        getSearchResult();
                        getLocalBooks(keyWord);
                        break;
                }
            }
        });
//        refreshLayout.setColorSchemeResources(R.color.colorBase, R.color.colorAccent, R.color.colorPrimary, R.color.colorBrowm);
//        refreshLayout.setOnRefreshListener(this);
//        refreshLayout.setOnLoadingListener(this);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBook=bookList.get(position);
                Intent intent=new Intent(SearchActivity.this,BookActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("bookinfo",mBook);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * 从豆瓣获取图书资源
     */
    public void getSearchResult(){
        loadingVIew.setVisibility(View.VISIBLE);
        String keyWord=editText.getText().toString().trim();
        String url= Constants.SearchBookApi+ "?q=" + keyWord + "&start=" + (mCurrentPageIndex - 1) * mPageSize +
                "&count=" + mPageSize;
        HttpUtil.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                setJSONData(s);
                if (adapter!=null){
                    adapter.notifyDataSetChanged();
                    loadingVIew.setVisibility(View.GONE);
                }else {
                    adapter=new BookAdapter(SearchActivity.this,bookList);
                    lvSearch.setAdapter(adapter);
                    loadingVIew.setVisibility(View.GONE);
                }

            }
        });
    }

    public void getLocalBooks(String text){
        MyUser user= BmobUser.getCurrentUser(this,MyUser.class);
        BmobQuery<Book> query=new BmobQuery<>();
        query.addWhereRelatedTo("likes",new BmobPointer(user));
        query.addWhereContains("title",text);
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size()!=0){
                    for (int i=0;i<list.size();i++){
                        bookList.add(list.get(i));
                    }
                    if (adapter!=null){
                        adapter.notifyDataSetChanged();
                        loadingVIew.setVisibility(View.GONE);
                    }else {
                        adapter=new BookAdapter(SearchActivity.this,bookList);
                        lvSearch.setAdapter(adapter);
                        loadingVIew.setVisibility(View.GONE);
                    }
                }else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void setJSONData(String data){
        try {
            JSONObject jsonObject=new JSONObject(data);
            JSONArray jsonArray=jsonObject.getJSONArray("books");
            for (int i=0;i<jsonArray.length();i++){
                mBook=new Book();
                setBookData(jsonArray.getJSONObject(i));
                mBook.save(SearchActivity.this);
                bookList.add(mBook);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setBookData(JSONObject obj){
        String author = "";
        try {
            JSONObject jsonObject = obj;
            JSONArray authorArray = jsonObject.getJSONArray("author");
            JSONArray tagArray = jsonObject.getJSONArray("tags");
            if (tagArray.length()>1){
                JSONObject tag1Obj = tagArray.getJSONObject(0);
                JSONObject tag2Obj = tagArray.getJSONObject(1);
                mBook.setTag1(tag1Obj.getString("name"));
                mBook.setTag2(tag2Obj.getString("name"));
            }
            mBook.setPages(jsonObject.getString("pages"));
            mBook.setTitle(jsonObject.getString("title"));
            mBook.setPrice(jsonObject.getString("price"));
            mBook.setSummary(jsonObject.getString("summary"));
            mBook.setPublisher(jsonObject.getString("publisher"));
            mBook.setPubdate(jsonObject.getString("pubdate"));
            mBook.setBookImage(jsonObject.optJSONObject("images").optString("large"));
            mBook.setCatalog(jsonObject.getString("catalog"));
            mBook.setIsbn(jsonObject.getString("isbn13"));
            for (int index = 0; index < authorArray.length(); index++) {
                author += authorArray.optString(index) + " ";
            }
            mBook.setAuthor(author);
            LogUtil.d(mBook.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        mCurrentPageIndex++;
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setLoading(false);
                getSearchResult();
            }
        }, 100);
    }

    @Override
    public void onRefresh() {
        mCurrentPageIndex=1;
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                bookList.clear();
                refreshLayout.setRefreshing(false);
                getSearchResult();
            }
        }, 2000);
    }
}
