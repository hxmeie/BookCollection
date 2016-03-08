package com.hxm.books.config;

import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by hxm on 2016/1/29.
 */
public class Constants {
    //AppId
    public static final String APP_ID="aeb5c4b32145065bb54ea83c40734e88";

    //接口
    public static final String GET_BOOK_BASE_URL="https://api.douban.com/v2/book/isbn/:";

    //缓存目录
    public static final File cacheDir= MyApplication.getInstance().getFilesDir();
    public static final File imageCacheDir=StorageUtils.getOwnCacheDirectory(MyApplication.getInstance(),"MyBook/image");

    //缓存文件名
    public static final String CACHE_BOOK_LIST="book_list";
}
