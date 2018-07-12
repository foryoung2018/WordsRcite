package com.hy.picture;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity  {

    @BindView(R.id.surfaceview)
    CameraSurfaceView surfaceview;
    @BindView(R.id.textureview)
    TextureView textureview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        CheckPermissionsUtil checkPermissionsUtil = new CheckPermissionsUtil(this);
        checkPermissionsUtil.requestAllPermission(this);
    }

    Camera camera;
    @OnClick(R.id.button) void onButtonClick() {
        // 打开摄像头并将展示方向旋转90度
        surfaceview.startRecord();
    }

    @OnLongClick(R.id.button) boolean onButtonLongClick() {
        //TODO implement
        return true;
    }

    @OnClick(R.id.button2) void onButton2Click() {
        //TODO implement
    }

    @OnLongClick(R.id.button2) boolean onButton2LongClick() {
        //TODO implement
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceview.closeCamera();
    }
}
