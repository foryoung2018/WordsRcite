package com.hy.picture.push;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.hy.picture.CamParaUtil;

import java.io.IOException;

public class VideoPusher extends Pusher implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private VideoParam videoParams;
    private byte[] buffers;
    private boolean isPushing = false;
    private PushNative pushNative;
    protected static final int[] VIDEO_320 = {320, 240};
    protected static final int[] VIDEO_480 = {640, 480};
    protected static final int[] VIDEO_720 = {1280, 720};
    protected static final int[] VIDEO_1080 = {1920, 1080};

    public VideoPusher(SurfaceHolder surfaceHolder, VideoParam videoParams, PushNative pushNative) {
        this.surfaceHolder = surfaceHolder;
        this.videoParams = videoParams;
        this.pushNative = pushNative;
        surfaceHolder.addCallback(this);
    }

    @Override
    public void startPush() {
        //设置视频参数
        pushNative.setVideoOptions(videoParams.getWidth(),
                videoParams.getHeight(), videoParams.getBitrate(), videoParams.getFps());
        isPushing = true;
    }

    @Override
    public void stopPush() {
        isPushing = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void release() {
        stopPreview();
    }


    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if(videoParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK){
            videoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }else{
            videoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        //重新预览
        stopPreview();
        startPreview();
    }

    /**
     * 开始预览
     */
    private void startPreview() {
        try {
            //SurfaceView初始化完成，开始相机预览
            mCamera = Camera.open(videoParams.getCameraId());
            Camera.Parameters parameters = mCamera.getParameters();
            //设置相机参数
            parameters.setPreviewFormat(ImageFormat.NV21); //YUV 预览图像的像素格式

            Camera.Size previewSize = CamParaUtil.getSize(parameters.getSupportedPreviewSizes(), 1000,
                    mCamera.new Size(VIDEO_720[0], VIDEO_720[1]));
            parameters.setPreviewSize(previewSize.width, previewSize.height);
//            parameters.setPreviewSize(videoParams.getWidth(), videoParams.getHeight()); //预览画面宽高
            mCamera.setParameters(parameters);
            //parameters.setPreviewFpsRange(videoParams.getFps()-1, videoParams.getFps());
            mCamera.setPreviewDisplay(surfaceHolder);
            //获取预览图像数据
            buffers = new byte[videoParams.getWidth() * videoParams.getHeight() * 4];
            mCamera.addCallbackBuffer(buffers);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止预览
     */
    private void stopPreview() {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(mCamera != null){
            mCamera.addCallbackBuffer(buffers);
        }

        if(isPushing){
            //回调函数中获取图像数据，然后给Native代码编码
            Log.i("*** yang","onPreviewFrame " +data);
            pushNative.fireVideo(data);
        }
    }


}
