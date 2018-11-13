package com.hy.picture;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.hy.picture.widget.GestureViewBinder;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextureviewActivity extends Activity  implements TextureView.SurfaceTextureListener{

    @BindView(R.id.textureview)
    TextureView textureview;
    @BindView(R.id.rlcontainer)
    RelativeLayout rlcontainer;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textureview);
        ButterKnife.bind(this);
        CheckPermissionsUtil checkPermissionsUtil = new CheckPermissionsUtil(this);
        checkPermissionsUtil.requestAllPermission(this);
        textureview.setSurfaceTextureListener(this);
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                Log.i("onPreviewFrame", Arrays.toString(bytes));
            }
        });
        getCallback();

        GestureViewBinder bind = GestureViewBinder.bind(this, rlcontainer, textureview);
        bind.setFullGroup(true);
    }

    private void getCallback() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(parameters);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            camera.setPreviewTexture(surface);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        camera.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
