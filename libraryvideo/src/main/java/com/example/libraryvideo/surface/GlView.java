package com.example.libraryvideo.surface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huizai on 2017/11/24.
 */

class GLRenderer implements GLSurfaceView.Renderer{
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        InitOpenGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        OnViewportChanged(i,i1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        RenderOneFrame();
    }

    /*********************************************************
     * jni 接口
     */
    static{
        System.loadLibrary("jni-lib");
    }
    public  native void InitOpenGL();
    public  native void OnViewportChanged(float width,float height);
    public  native void RenderOneFrame();
}

public class GlView extends GLSurfaceView {
    GLRenderer renderer;
    public GlView(Context context, AttributeSet attrs){
        super(context,attrs);
        setEGLContextClientVersion(2);
        renderer = new GLRenderer();
        setRenderer(renderer);
    }
}
