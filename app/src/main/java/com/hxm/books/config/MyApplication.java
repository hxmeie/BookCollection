package com.hxm.books.config;

import android.app.Application;
import android.content.Context;

import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import cn.bmob.v3.BmobUser;

/**
 * 自定义全局类
 * Created by hxm on 2016/1/12.
 */
public class MyApplication extends Application {

    public static MyApplication mInstance;
    public static MyUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        user = BmobUser.getCurrentUser(getInstance(),MyUser.class);
        //设置是否打印log
        LogUtil.isDebug=true;
        initImamgeLoader(getApplicationContext());
    }


    public static MyApplication getInstance() {
        return mInstance;
    }

    /**
     * 初始化ImageLoader配置信息
     * @param context
     */
    public static void initImamgeLoader(Context context){
        File cacheDir= StorageUtils.getOwnCacheDirectory(context,"MyBook/image_cache");
        ImageLoaderConfiguration.Builder config=new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()
        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
        .diskCacheSize(20 * 1024 * 1024) // 20 MiB
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .writeDebugLogs() //打印log信息, APP发布时删除这句
        .diskCache(new UnlimitedDiskCache(cacheDir));//自定义图片缓存路径
        //用配置信息初始化ImageLoader
        ImageLoader.getInstance().init(config.build());
    }
}
