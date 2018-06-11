package com.example.libraryvideo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.basecommonlibrary.CommonStation;
import com.example.basecommonlibrary.RouterCommonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: xiewenliang
 * @Filename:
 * @Description:
 * @Copyright: Copyright (c) 2017 Tuandai Inc. All rights reserved.
 * @date: 2017/4/25 14:29
 */
@Route(path = "/libraryOne/主页", extras = CommonStation.CHECK_LOADING)
public class ActivityOne extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_one_activity_main);
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
//        showOperationDialog();
        Toast.makeText(this,stringFromJNI(),Toast.LENGTH_LONG).show();
    }
    private void showOperationDialog(){
        List<String> list = new ArrayList<String>();
        LinkedList<String> data = new LinkedList<>();
        data.addFirst("12121");
        data.addFirst("12121");
        data.addFirst("12121");
        data.addFirst("12121");
        data.addFirst("12121");
        list.add("11");
        list.add("12");
        POperationDialog dialog = new POperationDialog(ActivityOne.this,data);
        dialog.show();
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt1) {
            RouterCommonUtil.startMainActivity(this);

        } else if (i == R.id.bt2) {
            RouterCommonUtil.startLibraryTwoActivity(this);
        }
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();

    /* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("jni-lib");
    }

    @Override
    protected void onResume() {
        super.onResume();

        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(ActivityOne.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    ActivityOne.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        String input = new File(Environment.getExternalStorageDirectory(),"video.mp4").getAbsolutePath();
        FFmpegTest(input,"");
    }

    static{
        System.loadLibrary("jni-lib");
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
        System.loadLibrary("fdk-aac");
    }
    public static native int JniCppAdd(int a,int b);
    public static native int JniCppSub(int a,int b);
    public static native int FFmpegTest(String input,String output);
}
