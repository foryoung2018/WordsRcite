package com.hy.picture;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hy.picture.push.LivePusher;
import com.hy.picture.push.LiveStateChangeListener;
import com.hy.picture.push.PushNative;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class PushActivity extends Activity implements LiveStateChangeListener {

    @BindView(R.id.surface) SurfaceView surface;
    @BindView(R.id.adcontainer) LinearLayout adcontainer;

    static final String URL = "rtmp://192.168.1.79/live/stream";
    private LivePusher live;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        ButterKnife.bind(this);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface);
        //相机图像的预览
        live = new LivePusher(surfaceView.getHolder());

    }

    @OnClick(R.id.btn_push) void onBtnPushClick() {
        //TODO implement
    }

    @OnLongClick(R.id.btn_push) boolean onBtnPushLongClick() {
        //TODO implement
        return true;
    }

    @OnClick(R.id.btn_camera_switch) void onBtnCameraSwitchClick() {
        //TODO implement
    }

    @OnLongClick(R.id.btn_camera_switch) boolean onBtnCameraSwitchLongClick() {
        //TODO implement
        return true;
    }

    static{
        System.loadLibrary("push");
    }


    /**
     * 开始直播
     * @param btn
     */
    public void mStartLive(View view) {
        Button btn = (Button)view;
        if(btn.getText().equals("开始直播")){
            live.startPush(URL,this);
            btn.setText("停止直播");
        }else{
            live.stopPush();
            btn.setText("开始直播");
        }
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PushNative.CONNECT_FAILED:
                    Toast.makeText(PushActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    //Log.d("jason", "连接失败..");
                    break;
                case PushNative.INIT_FAILED:
                    Toast.makeText(PushActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 切换摄像头
     * @param btn
     */
    public void mSwitchCamera(View btn) {
        live.switchCamera();
    }

    @Override
    public void onError(int code) {
        handler.sendEmptyMessage(code);

    }
}
