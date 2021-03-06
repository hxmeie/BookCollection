package com.hxm.books.config;

import android.app.Application;
import android.content.Context;

import com.hxm.books.R;
import com.hxm.books.listener.FirstDisplayListener;
import com.hxm.books.utils.LogUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 自定义全局类
 * Created by hxm on 2016/1/12.
 */
public class MyApplication extends Application {

    public static MyApplication mInstance;
    //    public static FileCache cache;
    public static boolean isRefresh=false;
    public static DisplayImageOptions options;
    public static ImageLoadingListener imageLoadingListener;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //设置是否打印log
        LogUtil.isDebug=true;
//        cache=FileCache.get(Constants.cacheDir);
        initImamgeLoader(getApplicationContext());
        imageLoadingListener = new FirstDisplayListener();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.no_cover)
                .showImageForEmptyUri(R.mipmap.no_cover)
                .showImageOnFail(R.mipmap.no_cover)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .displayer(new RoundedBitmapDisplayer(20))
//                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }


    public static MyApplication getInstance() {
        return mInstance;
    }

    /**
     * 初始化ImageLoader配置信息
     * @param context
     */
    public static void initImamgeLoader(Context context){
//        File cacheDir= StorageUtils.getOwnCacheDirectory(context,"MyBook/image");
        ImageLoaderConfiguration.Builder config=new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()
        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
        .diskCacheSize(20 * 1024 * 1024) // 20 MiB
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .writeDebugLogs() //打印log信息, APP发布时删除这句
        .diskCache(new UnlimitedDiskCache(Constants.imageCacheDir));//自定义图片缓存路径
        //用配置信息初始化ImageLoader
        ImageLoader.getInstance().init(config.build());
    }
}
