package com.hy.opengl;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLVideoRenderer  implements GLSurfaceView.Renderer
        , SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnVideoSizeChangedListener {

    private static final String TAG = "GLRenderer";
    private Context context;
    private int aPositionLocation;
    private int programId;
    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            1f, -1f, 0f,
            -1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
    };

    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    private final float[] textureVertexData = {
            1f, 0f,
            0f, 0f,
            1f, 1f,
            0f, 1f
    };
    private FloatBuffer textureVertexBuffer;
    private int uTextureSamplerLocation;
    private int aTextureCoordLocation;
    private int textureId;

    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;
    private float[] mSTMatrix = new float[16];
    private int uSTMMatrixHandle;

    private boolean updateSurface;
    private int screenWidth, screenHeight;

    public GLVideoRenderer(Context context, String videoPath) {
        this.context = context;
        synchronized (this) {
            updateSurface = false;
        }
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);

        initMediaPlayer(videoPath);
    }

    String vertexShader = "" +
            "attribute vec4 aPosition;//顶点位置\n" +
            "attribute vec4 aTexCoord;//S T 纹理坐标\n" +
            "varying vec2 vTexCoord;\n" +
            "uniform mat4 uMatrix;\n" +
            "uniform mat4 uSTMatrix;\n" +
            "void main() {\n" +
            "    vTexCoord = (uSTMatrix * aTexCoord).xy;\n" +
            "    gl_Position = uMatrix*aPosition;\n" +
            "}";

    String fragmentShader1 = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTexCoord;\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "void main() {\n" +
            "\n" +
            "      //  vec3 centralColor = texture2D(sTexture, vTexCoord).rgb;\n" +
            "     //   gl_FragColor = vec4(0.299*centralColor.r+0.587*centralColor.g+0.114*centralColor.b);\n" +
            "       //            vec4 src = texture2D(sTexture, vTexCoord);\n" +
            "       //             gl_FragColor = vec4(1.0 - src.r, 1.0 - src.g, 1.0 - src.b, 1.0);" +
            "        gl_FragColor=texture2D(sTexture, vTexCoord * cloneCount);\n" +
            "    //   gl_FragColor=texture2D(sTexture, vTexCoord );\n" +
            "\n" +
            "}";

    private final String fragmentShader =" " +
            "#extension GL_OES_EGL_image_external : require\n" +
            " precision mediump float;\n" +
            "                varying vec2 vTexCoord;\n" +
            "                uniform samplerExternalOES sTexture;\n" +
            "                uniform float intensity;\n" +
            "\n" +
            "                vec2 scale(vec2 srcCoord, float x, float y) {\n" +
            "                    return vec2((srcCoord.x - 0.5) / x + 0.5, (srcCoord.y - 0.5) / y + 0.5);\n" +
            "                }\n" +
            "\n" +
            "                void main() {\n" +
            "                    vec2 offsetTexCoord = scale(vTexCoord, intensity, intensity);\n" +
            "                    if (offsetTexCoord.x >= 0.0 && offsetTexCoord.x <= 1.0 &&\n" +
            "                        offsetTexCoord.y >= 0.0 && offsetTexCoord.y <= 1.0) {\n" +
            "           vec4 src = texture2D(sTexture, offsetTexCoord);" +
            "gl_FragColor = vec4(1.0 - src.r, 1.0 - src.g, 1.0 - src.b, 1.0);" +
            "   //                     gl_FragColor = texture2D(sTexture, offsetTexCoord);\n" +
            "                    }\n" +
            "                }";

    private int intensityLocation = 0;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        startTime = System.currentTimeMillis();
        programId = ShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionLocation = GLES20.glGetAttribLocation(programId, "aPosition");

        uMatrixLocation = GLES20.glGetUniformLocation(programId, "uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        uTextureSamplerLocation = GLES20.glGetUniformLocation(programId, "sTexture");
        aTextureCoordLocation = GLES20.glGetAttribLocation(programId, "aTexCoord");
        intensityLocation = GLES20.glGetUniformLocation(programId,"intensity");

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
   /*GLES11Ext.GL_TEXTURE_EXTERNAL_OES的用处？
      之前提到视频解码的输出格式是YUV的（YUV420p，应该是），那么这个扩展纹理的作用就是实现YUV格式到RGB的自动转化，
      我们就不需要再为此写YUV转RGB的代码了*/
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);//监听是否有新的一帧数据到来

        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);

    }

    private void   initMediaPlayer(String path) {
        mediaPlayer = new MediaPlayer();
        try {
//            AssetFileDescriptor afd = context.getAssets().openFd("big_buck_bunny.mp4");
            mediaPlayer.setDataSource(path);
//            String path = "http://192.168.1.254:8192";
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.setDataSource(TextureViewMediaActivity.videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnVideoSizeChangedListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: " + width + " " + height);
        screenWidth = width;
        screenHeight = height;
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }
    private long startTime = 0;
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this) {
            if (updateSurface) {
                surfaceTexture.updateTexImage();//获取新数据
                surfaceTexture.getTransformMatrix(mSTMatrix);//让新的纹理和纹理坐标系能够正确的对应,mSTMatrix的定义是和projectionMatrix完全一样的。
                updateSurface = false;
            }
        }
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);
        double intensity = Math.abs(Math.sin((System.currentTimeMillis() - startTime) / 1000.0)) + 0.5;
        Log.d(TAG, "onDrawFrame() called with: intensity = [" + intensity + "]");
        GLES20.glUniform1f(intensityLocation, (float) intensity);

        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);

        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glUniform1i(uTextureSamplerLocation, 0);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        updateProjection(width, height);
    }

    private void updateProjection(int videoWidth, int videoHeight) {
        float screenRatio = (float) screenWidth / screenHeight;
        float videoRatio = (float) videoWidth / videoHeight;
        if (videoRatio > screenRatio) {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -videoRatio / screenRatio, videoRatio / screenRatio, -1f, 1f);
        } else{
            Matrix.orthoM(projectionMatrix, 0, -screenRatio / videoRatio, screenRatio / videoRatio, -1f, 1f, -1f, 1f);

        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
