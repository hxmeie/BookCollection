package com.hxm.books;

import android.app.Application;
import android.content.Context;

import com.hxm.books.bean.MyUser;
import com.hxm.books.utils.LogUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
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
        ImageLoaderConfiguration.Builder config=new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY -2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(20 * 1024 * 1024); // 20 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); //打印log信息, APP发布时删除这句
        //用配置信息初始化ImageLoader
        ImageLoader.getInstance().init(config.build());
    }
}
