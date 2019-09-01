package com.hy.opengl;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OneActivity extends AppCompatActivity {


    @BindView(R.id.surfaceView)
    GLSurfaceView surfaceView;
    @BindView(R.id.btVideo)
    Button btVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        ButterKnife.bind(this);
        surfaceView.setEGLContextClientVersion(2);
        ImageTextureRender triangle = new ImageTextureRender(this);
//        Triangle triangle = new Triangle(this);
        surfaceView.setRenderer(triangle);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        EventBus.getDefault().register(this);
        btVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OneActivity.this, VideoActivity.class));
            }
        });

    }


//    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
//    public void handleEvent(String event) {
//        // do something
//        Log.i("eventbus",event);
//    }
}
