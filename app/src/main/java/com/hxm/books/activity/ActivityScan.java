package com.hxm.books.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.hxm.books.R;
import com.hxm.books.zxing.camera.CameraManager;
import com.hxm.books.zxing.decoding.CaptureActivityHandler;
import com.hxm.books.zxing.decoding.InactivityTimer;
import com.hxm.books.zxing.decoding.RGBLuminanceSource;
import com.hxm.books.zxing.view.ViewfinderView;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by hxm on 2016/1/13.
 */
public class ActivityScan extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

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


    private static final int REQUEST_CODE = 100;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
    private ProgressDialog mProgress;
    private String photo_path;
    private Bitmap scanBitmap;

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
        WeakReference<ActivityScan> mActivity;

        public MyHandler(ActivityScan activity) {
            mActivity = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActivityScan activityScan=mActivity.get();
            activityScan.mProgress.dismiss();
            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    activityScan.onResultHandler((String) msg.obj);
                    break;
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(activityScan, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            mProgress.dismiss();
//            switch (msg.what) {
//                case PARSE_BARCODE_SUC:
//                    onResultHandler((String) msg.obj);
//                    break;
//                case PARSE_BARCODE_FAIL:
//                    Toast.makeText(ActivityScan.this, (String) msg.obj, Toast.LENGTH_LONG).show();
//                    break;
//
//            }
//        }
//
//    };
//从手机里获取二维码图片并扫描
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case REQUEST_CODE:
//                    //获取选中图片的路径
//                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
//                    if (cursor.moveToFirst()) {
//                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                    }
//                    cursor.close();
//
//                    mProgress = new ProgressDialog(ActivityScan.this);
//                    mProgress.setMessage(stringId(this, R.string.scaning));
//                    mProgress.setCancelable(false);
//                    mProgress.show();
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Result result = scanningImage(photo_path);
//                            if (result != null) {
//                                Message m = mHandler.obtainMessage();
//                                m.what = PARSE_BARCODE_SUC;
//                                m.obj = result.getText();
//                                mHandler.sendMessage(m);
//                            } else {
//                                Message m = mHandler.obtainMessage();
//                                m.what = PARSE_BARCODE_FAIL;
//                                m.obj = "Scan failed!";
//                                mHandler.sendMessage(m);
//                            }
//                        }
//                    }).start();
//
//                    break;
//
//            }
//        }
//    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
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
     * 跳转到图书信息界面
     *
     * @param resultString
     */

    private void onResultHandler(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(ActivityScan.this, stringId(this,R.string.scan_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent resultIntent = new Intent(ActivityScan.this,BookDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("result", resultString);
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        startAnimActivity(resultIntent);
        ActivityScan.this.finish();
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
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}
