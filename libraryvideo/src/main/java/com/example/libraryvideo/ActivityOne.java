package com.example.libraryvideo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.basecommonlibrary.CommonStation;
import com.example.basecommonlibrary.RouterCommonUtil;
import com.example.libraryvideo.shape.Square;
import com.example.libraryvideo.shape.Triangle;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author: xiewenliang
 * @Filename:
 * @Description:
 * @Copyright: Copyright (c) 2017 Tuandai Inc. All rights reserved.
 * @date: 2017/4/25 14:29
 */
@Route(path = "/libraryOne/主页", extras = CommonStation.CHECK_LOADING)
public class ActivityOne extends AppCompatActivity implements View.OnClickListener {
    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.library_one_activity_main);
//        findViewById(R.id.bt1).setOnClickListener(this);
//        findViewById(R.id.bt2).setOnClickListener(this);
//        showOperationDialog();
// Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
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

    class MyGLSurfaceView extends GLSurfaceView {

        private final MyGLRenderer mRenderer;

        public MyGLSurfaceView(Context context){
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MyGLRenderer();

            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(mRenderer);
        }
    }

    public class MyGLRenderer implements GLSurfaceView.Renderer {

        private Triangle mTriangle;
        private Square mSquare;

        // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
        private final float[] mMVPMatrix = new float[16];
        private final float[] mProjectionMatrix = new float[16];
        private final float[] mViewMatrix = new float[16];

        public void onDrawFrame(GL10 unused) {
            // Redraw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//            mTriangle.draw();

            // Set the camera position (View matrix)
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Calculate the projection and view transformation
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            // Draw shape
            mTriangle.draw(mMVPMatrix);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
            // Set the background frame color
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            // initialize a triangle
            mTriangle = new Triangle();
            // initialize a square
            mSquare = new Square();
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;

            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        }
    }
}
