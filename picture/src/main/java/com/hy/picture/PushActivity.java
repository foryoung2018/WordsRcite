package com.hy.picture;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hy.picture.camera.FocusView;
import com.hy.picture.push.LivePusher;
import com.hy.picture.push.LiveStateChangeListener;
import com.hy.picture.push.PushNative;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class PushActivity extends Activity implements LiveStateChangeListener {

    @BindView(R.id.surface)SurfaceView surface;
    @BindView(R.id.adcontainer) LinearLayout adcontainer;
    @BindView(R.id.view_focus) FocusView view_focus;

    static final String URL = "rtmp://192.168.1.102/live/stream";
    private LivePusher live;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        ButterKnife.bind(this);
        CheckPermissionsUtil checkPermissionsUtil = new CheckPermissionsUtil(this);
        checkPermissionsUtil.requestAllPermission(this);
        //相机图像的预览
        live = new LivePusher(surface.getHolder());
        checkPermissions();

    }

    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int MY_PERMISSIONS_REQUEST = 1001;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("*** yang", permissions[i] + " 权限被用户禁止！");
                }
            }
            // 运行时权限的申请不是本demo的重点，所以不再做更多的处理，请同意权限申请。
        }
    }

    /**
     * 被用户拒绝的权限列表
     */
    private List<String> mPermissionList = new ArrayList<>();

    private void checkPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
    }

//    @OnClick(R.id.btn_push) void onBtnPushClick() {
//        //TODO implement
//    }
//
//    @OnLongClick(R.id.btn_push) boolean onBtnPushLongClick() {
//        //TODO implement
//        return true;
//    }
//
//    @OnClick(R.id.btn_camera_switch) void onBtnCameraSwitchClick() {
//        //TODO implement
//    }
//
//    @OnLongClick(R.id.btn_camera_switch) boolean onBtnCameraSwitchLongClick() {
//        //TODO implement
//        return true;
//    }

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
