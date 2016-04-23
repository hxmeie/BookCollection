package com.hxm.books.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hxm.books.bean.BookISBN;
import com.hxm.books.config.Constants;
import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.utils.CommonUtils;
import com.hxm.books.utils.HttpUtil;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.zxing.camera.CameraManager;
import com.hxm.books.zxing.decoding.CaptureActivityHandler;
import com.hxm.books.zxing.decoding.InactivityTimer;
import com.hxm.books.zxing.view.ViewfinderView;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Vector;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by hxm on 2016/1/13.
 */
public class ScanActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private String TAG="ScanActivity";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Book mBook;
    private static final int REQUEST_CODE = 100;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
    private ImageButton btnFlashlight;
    private int tag = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        mBook = new Book();
        initView();
    }

    public void initView() {
        initOnlyTitleAndLeftBar(stringId(this, R.string.activity_scan_title));
        btnFlashlight = (ImageButton) findViewById(R.id.btn_flashlight);

        btnFlashlight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_flashlight:
                switch (tag) {
                    case 0:
                        CameraManager.get().openLight();
                        btnFlashlight.setImageResource(R.mipmap.flashlight_on);
                        tag = 1;
                        break;
                    case 1:
                        CameraManager.get().offLight();
                        btnFlashlight.setImageResource(R.mipmap.flashlight_off);
                        tag = 0;
                        break;
                }
                break;
        }

    }

    //    弱引用
    private MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        WeakReference<ScanActivity> mActivity;

        public MyHandler(ScanActivity activity) {
            mActivity = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ScanActivity scanActivity = mActivity.get();
            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    scanActivity.onResultHandler((String) msg.obj);
                    break;
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(scanActivity, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        //震动
        vibrate = false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     */

    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        onResultHandler(resultString);
    }

    /**
     * 处理扫描结果，跳转到图书信息界面
     *
     * @param resultString
     */

    private void onResultHandler(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(ScanActivity.this, stringId(this, R.string.scan_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        getBookData(resultString);
    }

    private void getBookData(final String result) {
        CameraManager.get().stopPreview();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("已扫描,查询中...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final String url = Constants.GET_BOOK_BASE_URL + result;
        BmobQuery<Book> query=new BmobQuery<>();
        query.addWhereEqualTo("isbn",result);
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                if (list.size()==0){
                    LogUtil.i("ScanActivity","get book"+mBook.getTitle()+" from douban api");
                    getDataFromDouban(url,dialog);
                }
                mBook=list.get(0);
                LogUtil.i("ScanActivity","get book"+mBook.getTitle()+" from my database");
                Intent resultIntent = new Intent(ScanActivity.this, ScanBookDetailsActivity.class);
                resultIntent.putExtra("book_isbn", mBook.getIsbn());
                startAnimActivity(resultIntent);
                dialog.dismiss();
                finish();

            }

            @Override
            public void onError(int i, String s) {
                getDataFromDouban(url,dialog);
            }
        });

    }

    /**
     * 如果数据库中没有图书信息，则从豆瓣获取
     * @param str
     * @param dialog
     */
    private void getDataFromDouban(String str, final ProgressDialog dialog){
        HttpUtil.get(str, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                String failureMsg = null;
                if (CommonUtils.isNetworkAvailable(ScanActivity.this)) {
                    failureMsg = s;
                } else {
                    failureMsg = "网络未连接";
                }
                LogUtil.e(TAG,"获取失败" + s);
                dialog.dismiss();
                final NormalDialog mDialog = new NormalDialog(ScanActivity.this);
                mDialog.btnNum(1)
                        .showAnim(null)
                        .dismissAnim(null)
                        .content(failureMsg)
                        .btnText("确定")
                        .show();
                mDialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        ScanActivity.this.finish();
                        mDialog.dismiss();
                    }
                });
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                LogUtil.i(s);
                setBookData(s);
                updateBookInfoToServer();
                dialog.dismiss();
                Intent resultIntent = new Intent(ScanActivity.this, ScanBookDetailsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("bookObject", mBook);
                resultIntent.putExtra("book_isbn", mBook.getIsbn());
                startAnimActivity(resultIntent);
                finish();
            }


        });
    }

    private void setBookData(String bookInfo) {
        String author = "";
        try {
            JSONObject jsonObject = new JSONObject(bookInfo);
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
                    mBook.save(ScanActivity.this);
                    BookISBN bookISBN =new BookISBN();
                    bookISBN.setBookISBN(mBook.getIsbn());
                    bookISBN.save(ScanActivity.this);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.i(TAG,"上传失败"+s);
            }
        });

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, release it.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            //mediaPlayer.seekTo(0);
            mediaPlayer.release();
        }
    };
}
