package com.hxm.books.activity;

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
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hxm.books.Constants;
import com.hxm.books.R;
import com.hxm.books.bean.Book;
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
    private Book  mBook;
    private static final int REQUEST_CODE = 100;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
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
        mBook= new Book();
        initView();
    }

    public void initView(){
        initOnlyTitleAndLeftBar(stringId(this, R.string.activity_scan_title));
    }

    @Override
    public void onClick(View v) {


    }

//    弱引用
    private MyHandler mHandler =new MyHandler(this);

    static class MyHandler extends Handler {
        WeakReference<ScanActivity> mActivity;

        public MyHandler(ScanActivity activity) {
            mActivity = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ScanActivity scanActivity =mActivity.get();
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
            Toast.makeText(ScanActivity.this, stringId(this,R.string.scan_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        getBookData(resultString);
    }

    private void getBookData(String result){
        String url= Constants.GET_BOOK_BASE_URL+result;
        HttpUtil.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtil.e("获取失败");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                LogUtil.i(s);
                setBookData(s);
                updateBookInfoToServer();
                Intent resultIntent = new Intent(ScanActivity.this, ScanBookDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookObject", mBook);
                resultIntent.putExtras(bundle);
                startAnimActivity(resultIntent);
                ScanActivity.this.finish();
            }


        });
    }
    private void setBookData(String bookInfo){
        String author="";
        try {
            JSONObject jsonObject =new JSONObject(bookInfo);
            JSONArray authorArray=jsonObject.getJSONArray("author");
            JSONArray tagArray=jsonObject.getJSONArray("tags");
            JSONObject tag1Obj=tagArray.getJSONObject(0);
            JSONObject tag2Obj=tagArray.getJSONObject(1);
            mBook.setPages(jsonObject.getString("pages"));
            mBook.setTitle(jsonObject.getString("title"));
            mBook.setPrice(jsonObject.getString("price"));
            mBook.setSummary(jsonObject.getString("summary"));
            mBook.setPublisher(jsonObject.getString("publisher"));
            mBook.setPubdate(jsonObject.getString("pubdate"));
            mBook.setBookImage(jsonObject.optJSONObject("images").optString("large"));
            mBook.setCatalog(jsonObject.getString("catalog"));
            mBook.setIsbn(jsonObject.getString("isbn13"));
            for (int index=0;index<authorArray.length();index++){
                author += authorArray.optString(index)+" ";
            }
            mBook.setAuthor(author);

            mBook.setTag1(tag1Obj.getString("name"));
            mBook.setTag2(tag2Obj.getString("name"));

            LogUtil.d(mBook.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将书籍信息上传到服务器
     */
    private void updateBookInfoToServer(){
        BmobQuery<Book> query = new BmobQuery<>();
        query.addWhereEqualTo("isbn",mBook.getIsbn());
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                LogUtil.i("上传","查询成功返回结果数" +list.size());
                if (list.size()==0) {
                    mBook.save(ScanActivity.this);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.i("上传",s );
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
