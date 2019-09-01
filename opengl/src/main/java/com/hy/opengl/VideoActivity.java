package com.hy.opengl;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {


    private static final String TAG = "videoAct";
    @BindView(R.id.surfaceView)
    GLSurfaceView surfaceView;
    @BindView(R.id.btVideo)
    Button btVideo;
    VideoPlayer videoPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        ButterKnife.bind(this);
        surfaceView.setEGLContextClientVersion(2);
        videoPlayer = new VideoPlayer();
//        surfaceView.setRenderer(new VideoRender(videoPlayer,this));

        TedPermission.with(this).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                pickVideo();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        }).setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).check();

    }
    private int REQUEST_PICK_VIDEO = 1124;
    private void pickVideo() {
        Intent intent =new  Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(intent, REQUEST_PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_VIDEO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                handleImage(data);
            } else {
                finish();
            }
        }
    }

    private void handleImage(Intent intent) {
            Uri uri = intent.getData();
            if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                String path = uri.getPath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    path = FileUtils.getPath(getApplicationContext(), uri);
                } else {
                    path = uri.getPath();
                }
                onVideoPicked(path);
                return;
            }
            return;
    }

    private void onVideoPicked(String path) {
        Log.d(TAG, "onVideoPicked() called with: path = [" + path + "]");
        surfaceView.setRenderer(new GLVideoRenderer(this, path));
//        try {
//            videoPlayer.setDataSource(path);
//            videoPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                Log.d(TAG, "onPrepared() called with: mp = [" + mp + "]");
//                videoPlayer.start();
//            }
//        });
    }

    public static class VideoPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener{


        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    }
}
