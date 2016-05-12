package com.hxm.books.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.hxm.books.R;
import com.hxm.books.bean.Book;
import com.hxm.books.bean.MyUser;
import com.hxm.books.config.Constants;
import com.hxm.books.config.MyApplication;
import com.hxm.books.utils.FileUtil;
import com.hxm.books.utils.ImageUtils;
import com.hxm.books.utils.LogUtil;
import com.hxm.books.utils.RegexpUtils;
import com.hxm.books.utils.StringUtils;
import com.hxm.books.utils.ToastUtils;
import com.hxm.books.view.ClearEditText;
import com.hxm.books.view.HeaderLayout;
import com.hxm.books.view.loadingindicator.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 如果扫描图书没有结果，则可以自己手动添加图书
 * Created by hxm on 2016/5/6.
 */
public class AddNewBookActivity extends BaseActivity {
    private ClearEditText setBook, setISBN, setAuthor, setClassify, setPages, setPrice, setPublishDate, setPublishingCompany, setBookSummary, setBookCatalog;
    private Button btnCommit;
    private LinearLayout setImage;
    private AVLoadingIndicatorView loadingView;
    private ImageView bookPic;
    private String[] mString = new String[]{"从相册中选择", "打开相机拍照"};
    private final String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/MyBook/Portrait/";
    private final String THUMBNAIL_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/MyBook/Thumbnail/";
    private final String CAMERA_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/MyBook/Camera/";
    private Uri origUri;
    private Uri cropUri;
    private File protraitFile;
    private Bitmap protraitBitmap;
    private String protraitPath;
    private String newBookImageUrl;
    private String timeStamp;
    private String key;
    private boolean IS_CHECK_BOOK_PIC = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_book);
        makeDir();
        initView();
    }

    private void makeDir() {
        File thumbnail = new File(THUMBNAIL_PATH);
        File camera = new File(CAMERA_PATH);
        File protrait = new File(FILE_SAVEPATH);
        if (!thumbnail.exists()) {
            thumbnail.mkdirs();
        }
        if (!camera.exists()) {
            camera.mkdirs();
        }
        if (!protrait.exists()) {
            protrait.mkdirs();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.clearFileWithPath(THUMBNAIL_PATH);
        FileUtil.clearFileWithPath(FILE_SAVEPATH);
        FileUtil.clearFileWithPath(CAMERA_PATH);
    }

    private void initView() {
//        initOnlyTitleAndLeftBar("添加新书");
        initBothLeftAndRightBar("添加新书", "清空", R.color.transparent, new HeaderLayout.headerLayoutRightOnclickLister() {
            @Override
            public void onClick() {
                setEmptyText();
            }
        });
        setBook = (ClearEditText) findViewById(R.id.ev_set_book_name);
        setISBN = (ClearEditText) findViewById(R.id.ev_set_isbn);
        setAuthor = (ClearEditText) findViewById(R.id.ev_set_author);
        setClassify = (ClearEditText) findViewById(R.id.ev_set_classify);
        setPages = (ClearEditText) findViewById(R.id.ev_set_pages);
        setPrice = (ClearEditText) findViewById(R.id.ev_set_price);
        setPublishDate = (ClearEditText) findViewById(R.id.ev_set_publish_date);
        setPublishingCompany = (ClearEditText) findViewById(R.id.ev_set_publishing_company);
        setBookSummary = (ClearEditText) findViewById(R.id.ev_set_summary);
        setBookCatalog = (ClearEditText) findViewById(R.id.ev_set_catalog);
        btnCommit = (Button) findViewById(R.id.btn_set_book_commit);
        setImage = (LinearLayout) findViewById(R.id.set_book_pic);
        loadingView = (AVLoadingIndicatorView) findViewById(R.id.add_new_book_loading_view);
        bookPic = (ImageView) findViewById(R.id.iv_set_book_pic);

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IS_CHECK_BOOK_PIC) {
                    ToastUtils.show(AddNewBookActivity.this, "图片必须添加！");
                    return;
                }
                if (getText(setBook).isEmpty() || getText(setISBN).isEmpty() || getText(setClassify).isEmpty()) {
                    ToastUtils.show(AddNewBookActivity.this, "必填项不能为空！");
                    return;
                }
                if (!RegexpUtils.isRegexpValidate(getText(setISBN), RegexpUtils.ISBN)) {
                    ToastUtils.show(AddNewBookActivity.this, "ISBN号填写不正确！");
                    return;
                }
                savePicToServer();
            }
        });

        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalListDialog();
            }
        });

    }

    private void normalListDialog() {
        final NormalListDialog dialog = new NormalListDialog(this, mString);
        dialog.title("请选择")
                .showAnim(null)
                .dismissAnim(null)
                .layoutAnimation(null)
                .itemTextSize(18)
                .isTitleShow(false)
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //从相册中获取
                        getPictureFromGallery();
                        dialog.dismiss();
                        break;
                    case 1:
                        //打开相机
                        getPictureFromCamera();
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }
    /**
     * 从相册中获取图片
     */
    private void getPictureFromGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    Constants.PHOTO_REQUEST_GALLERY);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    Constants.PHOTO_REQUEST_GALLERY);
        }
    }

    private void getPictureFromCamera() {
        Intent intent;
        // 判断是否挂载了SD卡
//        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
//            savePath = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + "/MyBook/Camera/";
            File savedir = new File(CAMERA_PATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (StringUtils.isEmpty(CAMERA_PATH)) {
            ToastUtils.show(this, "无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = "book_" + timeStamp + ".jpg";// 照片命名
        File out = new File(CAMERA_PATH, fileName);
        Uri uri = Uri.fromFile(out);
        origUri = uri;
        //savePath + fileName 该照片的绝对路径
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                Constants.PHOTO_REQUEST_CAREMA);
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     */
    private void startActionCrop(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 3);// 裁剪框比例
        intent.putExtra("aspectY", 4);
        intent.putExtra("outputX", 600);// 输出图片大小
        intent.putExtra("outputY", 800);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                Constants.PHOTO_REQUEST_CUT);
    }

    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            ToastUtils.show(this, "无法保存上传的图片，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (StringUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(this, uri);
        }
        String ext = FileUtil.getFileFormat(thePath);//获取文件扩展名
        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "book_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);

        cropUri = Uri.fromFile(protraitFile);
        return this.cropUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case Constants.PHOTO_REQUEST_GALLERY:
                startActionCrop(data.getData());
                break;
            case Constants.PHOTO_REQUEST_CAREMA:
                startActionCrop(origUri);// 拍照后裁剪
                break;
            case Constants.PHOTO_REQUEST_CUT:
                IS_CHECK_BOOK_PIC = true;
                uploadNewPhoto();
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadNewPhoto() {
        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        key = "bitmap_thumbnail" + timeStamp + ".png";
        // 获取头像缩略图
        if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
            protraitBitmap = ImageUtils
                    .loadImgThumbnail(protraitPath, 150, 200);
            try {
                saveBitmap(protraitBitmap, key);
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.show(this, "保存临时缩略图失败");
            }
        } else {
            ToastUtils.show(this, "图像不存在，上传失败");
        }
        if (protraitBitmap != null) {
            bookPic.setImageBitmap(protraitBitmap);
            File file = new File(THUMBNAIL_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    private void savePicToServer() {
        loadingView.setVisibility(View.VISIBLE);
        final BmobFile bmobFile = new BmobFile(new File(THUMBNAIL_PATH + key));
        bmobFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                IS_CHECK_BOOK_PIC = false;
                newBookImageUrl = bmobFile.getFileUrl(AddNewBookActivity.this);
                LogUtil.i("file_upload", "success" + newBookImageUrl);
                queryStarBook();
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i("file_upload", "failed" + s);
            }
        });
        LogUtil.i("file_upload", protraitBitmap.getByteCount() + "");
    }

    private void saveBitmap(Bitmap bitmap, String bitName) throws IOException {
        File file = new File(THUMBNAIL_PATH + bitName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询当前用户是否收藏该书
     */
    private void queryStarBook() {
        BmobQuery<Book> query = new BmobQuery<>();
        MyUser user = BmobUser.getCurrentUser(MyApplication.getInstance(), MyUser.class);
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.addQueryKeys("isbn");
        query.addWhereEqualTo("isbn", getText(setISBN));
        query.findObjects(this, new FindListener<Book>() {
            @Override
            public void onSuccess(List<Book> list) {
                loadingView.setVisibility(View.GONE);
                LogUtil.i("list_size", list.size() + "");
                if (list.size() != 0) {
                    if (getText(setISBN).equals(list.get(0).getIsbn())) {
                        final NormalDialog dialog = new NormalDialog(AddNewBookActivity.this);
                        dialog.title("提示")
                                .content("书籍已存在书架中")
                                .showAnim(null)
                                .dismissAnim(null)
                                .btnNum(1)
                                .btnText("确定")
                                .show();
                        dialog.setOnBtnClickL(new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        });
                    }
                } else {
                    addBookToBookShelf();
                }

            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.show(AddNewBookActivity.this, "失败" + s);
            }
        });
    }

    public void addBookToBookShelf() {
        final Book book = new Book();
        book.setTitle(getText(setBook));
        book.setIsbn(getText(setISBN));
        book.setAuthor(getText(setAuthor));
        book.setTag1(getText(setClassify));
        book.setPages(getText(setPages));
        book.setPrice(getText(setPrice));
        book.setPubdate(getText(setPublishDate));
        book.setPublisher(getText(setPublishingCompany));
        book.setSummary(getText(setBookSummary));
        book.setCatalog(getText(setBookCatalog));
        book.setIdentifyBook("自定义");
        book.setBookImage(newBookImageUrl);
        book.save(AddNewBookActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                MyUser user = BmobUser.getCurrentUser(AddNewBookActivity.this, MyUser.class);
                BmobRelation relation = new BmobRelation();
                relation.add(book);
                user.setLikes(relation);
                user.update(AddNewBookActivity.this);
                final NormalDialog dialog = new NormalDialog(AddNewBookActivity.this);
                dialog.btnText("确定")
                        .btnNum(1)
                        .title("提示")
                        .content("上传成功")
                        .showAnim(null)
                        .dismissAnim(null)
                        .show();
                dialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        bookPic.setImageResource(R.mipmap.no_cover);
                        setEmptyText();
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.show(AddNewBookActivity.this, "失败" + s);
            }
        });
    }

    public void setEmptyText() {
        setBook.setText("");
        setISBN.setText("");
        setClassify.setText("");
        setAuthor.setText("");
        setPages.setText("");
        setPrice.setText("");
        setPublishDate.setText("");
        setPublishingCompany.setText("");
        setBookSummary.setText("");
        setBookCatalog.setText("");

    }

    private String getText(ClearEditText editText) {
        return editText.getText().toString().trim();
    }
}
