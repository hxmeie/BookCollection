/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hxm.books.zxing.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.zxing.Result;
import com.hxm.books.R;
import com.hxm.books.activity.AddNewBookActivity;
import com.hxm.books.activity.BaseActivity;
import com.hxm.books.activity.ScanBookDetailsActivity;
import com.hxm.books.bean.Book;
import com.hxm.books.config.Constants;
import com.hxm.books.utils.HttpUtil;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.zxing.camera.CameraManager;
import com.hxm.books.zxing.decode.DecodeThread;
import com.hxm.books.zxing.utils.BeepManager;
import com.hxm.books.zxing.utils.CaptureActivityHandler;
import com.hxm.books.zxing.utils.InactivityTimer;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private ImageButton btnFlashlight;
    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private int tag = 0;
    private Book mBook;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);
        initOnlyTitleAndLeftBar("扫描图书");
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        mBook = new Book();
        btnFlashlight = (ImageButton) findViewById(R.id.btn_flashlight);

        btnFlashlight.setOnClickListener(this);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(2500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
//        Intent resultIntent = new Intent();
//        bundle.putInt("width", mCropRect.width());
//        bundle.putInt("height", mCropRect.height());
//        bundle.putString("result", rawResult.getText());
//        resultIntent.putExtras(bundle);
//        this.setResult(RESULT_OK, resultIntent);
        getBookData(rawResult.getText());
    }

    private void getBookData(final String result) {
        this.getCameraManager().stopPreview();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("已扫描,查询中...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final String url = Constants.GET_BOOK_BASE_URL + result;
        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereEqualTo("isbn", result);
        query.addQueryKeys("isbn");
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size() == 0) {
                    LogUtil.i("CaptureActivity", "get book" + mBook.getIsbn() + " from douban api");
                    getDataFromDouban(url, dialog);
                } else {
                    mBook = list.get(0);
                    LogUtil.i("CaptureActivity", "get book" + mBook.getIsbn() + " from my database");
                    Intent resultIntent = new Intent(CaptureActivity.this, ScanBookDetailsActivity.class);
                    resultIntent.putExtra("book_isbn", mBook.getIsbn());
                    startAnimActivity(resultIntent);
                    dialog.dismiss();
                    CaptureActivity.this.finish();
                }
            }

            @Override
            public void onError(int i, String s) {
                getDataFromDouban(url, dialog);
            }
        });

    }

    /**
     * 如果数据库中没有图书信息，则从豆瓣获取
     *
     * @param str
     * @param dialog
     */
    private void getDataFromDouban(String str, final ProgressDialog dialog) {
        HttpUtil.get(str, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtil.e(TAG, "获取失败" + s);
                dialog.dismiss();
                final NormalDialog mDialog = new NormalDialog(CaptureActivity.this);
                mDialog.showAnim(null)
                        .dismissAnim(null)
                        .content("未查到书籍，是否手动添加？")
                        .show();
                mDialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        mDialog.dismiss();
                        CaptureActivity.this.finish();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        CaptureActivity.this.finish();
                        mDialog.dismiss();
                        startActivity(new Intent(CaptureActivity.this, AddNewBookActivity.class));
                    }
                });
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                LogUtil.i(s);
                setBookData(s);
                updateBookInfoToServer();
                dialog.dismiss();
                Intent resultIntent = new Intent(CaptureActivity.this, ScanBookDetailsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("bookObject", mBook);
                resultIntent.putExtra("book_isbn", mBook.getIsbn());
                startAnimActivity(resultIntent);
                CaptureActivity.this.finish();
            }


        });
    }

    private void setBookData(String bookInfo) {
        String author = "";
        try {
            JSONObject jsonObject = new JSONObject(bookInfo);
            JSONArray authorArray = jsonObject.getJSONArray("author");
            JSONArray tagArray = jsonObject.getJSONArray("tags");
            if (tagArray.length() > 0) {
                JSONObject tag1Obj = tagArray.getJSONObject(0);
                String tag1 = tag1Obj.getString("name");
                if (tag1 == null || tag1 == "" || tag1 == "null" || tag1.isEmpty()) {
                    tag1 = "未分类";
                }
                mBook.setTag1(tag1);
            } else {
                mBook.setTag1("未分类");
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

    /**
     * 将书籍信息上传到服务器
     */
    private void updateBookInfoToServer() {
        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereEqualTo("isbn", mBook.getIsbn());
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                LogUtil.i(TAG, "查询list_size " + list.size());
                if (list.size() == 0) {
                    mBook.save(CaptureActivity.this);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.i(TAG, "上传失败" + s);
            }
        });

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_flashlight:
                switch (tag) {
                    case 0:
                        this.getCameraManager().openLight();
                        btnFlashlight.setImageResource(R.mipmap.flashlight_on);
                        tag = 1;
                        break;
                    case 1:
                        this.getCameraManager().offLight();
                        btnFlashlight.setImageResource(R.mipmap.flashlight_off);
                        tag = 0;
                        break;
                }
                break;
        }
    }
}