package com.fatto.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fatto.library.utils.luban.Luban;
import com.fatto.library.utils.luban.OnCompressListener;

import java.io.File;

/**
 * TODO
 *
 * @author fattoliu
 * @version V 1.0
 * @date on 30/7/2018 上午8:58
 */
public class H5TransferCamera extends AppCompatActivity {
    private WebView mWebView;
    private File mImageFile;
    private Uri mImageUri;
    /**
     * 拍照/选择文件请求码
     */
    private final int REQUEST_CODE_CAPTURE = 1;
    private final int REQUEST_SELECT_PHOTO = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_h5_transfer_camera);
        mWebView = findViewById(R.id.web_view_content);
        initSetting();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initSetting() {
        WebSettings settings = mWebView.getSettings();

        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        settings.setBuiltInZoomControls(true);// 启用触控缩放
        settings.setUseWideViewPort(true);// 启用支持视窗meta标记（可实现双击缩放）
        settings.setLoadWithOverviewMode(true);// 以缩略图模式加载页面
        settings.setJavaScriptEnabled(true);// 启用JavaScript支持
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        settings.setSupportZoom(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        // 添加 js 调用 java 的接口
        mWebView.addJavascriptInterface(this, "native");
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        //加载地址
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            view.loadUrl(url);
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Toast.makeText(H5TransferCamera.this, "弹出成功！", Toast.LENGTH_SHORT).show();
            return super.onJsAlert(view, url, message, result);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Luban.with(this)
                    .load(mImageFile.getAbsolutePath())  // 传人要压缩的图片列表
                    .ignoreBy(100)  // 忽略不压缩图片的大小
                    .setRename(false) // 是否重命名(true:不会覆盖原文件，false:覆盖原文件)
                    .setTargetDir(mImageFile.getParent()) // 设置压缩后文件存储位置
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(File file) {
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(mImageUri);
                            sendBroadcast(intent);
                            mWebView.loadUrl("javascript:setImage('" + mImageFile.getAbsolutePath() + "')");
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    }).launch();    //启动压缩
        } else if (requestCode == REQUEST_SELECT_PHOTO && resultCode == Activity.RESULT_OK
                && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumns, null,
                        null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
                    String imagePath = cursor.getString(columnIndex);
                    mWebView.loadUrl("javascript:setImage('" + imagePath + "')");
                    cursor.close();
                }
            }
        }
    }

    /**
     * js 关闭 activity 接口方法
     */
    @JavascriptInterface
    public void openCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        File imageDir = new File(/*Environment.getExternalStorageDirectory(), "MMST/H5_IMAGES"*/galleryPath);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        mImageFile = new File(imageDir, System.currentTimeMillis() + ".jpg");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageUri = FileProvider.getUriForFile(this, "com.fatto.android.provider", mImageFile);
        } else {
            mImageUri = Uri.fromFile(mImageFile);
        }
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        // 授予目录临时共享权限
        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(openCameraIntent, REQUEST_CODE_CAPTURE);
        Toast.makeText(this, "open camera success", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void selectPhotoFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
    }
}
