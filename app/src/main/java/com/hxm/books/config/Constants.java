package com.hxm.books.config;

import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by hxm on 2016/1/29.
 */
public class Constants {
    //AppId
    public static final String APP_ID="aeb5c4b32145065bb54ea83c40734e88";

    //time day
    public static final int TIME_DAY=60*60*24;

    //接口
    public static final String GET_BOOK_BASE_URL="https://api.douban.com/v2/book/isbn/:";

    //缓存目录
    public static final File cacheDir= MyApplication.getInstance().getFilesDir();
    public static final File imageCacheDir=StorageUtils.getOwnCacheDirectory(MyApplication.getInstance(),"MyBook/image");

    //缓存文件名
    public static final String CACHE_BOOK_LIST="book_list";

    /**
     * 图书搜索
     * <p>
     * q	查询关键字	q和tag必传其一<br/>
     * tag	查询的tag	q和tag必传其一<br/>
     * start	取结果的offset	默认为0<br/>
     * count	取结果的条数	默认为20，最大为100
     */
    public static String SearchBookApi = "https://api.douban.com/v2/book/search";

}
