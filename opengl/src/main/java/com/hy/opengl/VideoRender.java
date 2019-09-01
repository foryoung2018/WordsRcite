package com.hy.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.Surface;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private final Context context;
    VideoActivity.VideoPlayer videoPlayer;

    private final String VERTEX_SHADER = " attribute vec4 a_Position;\n" +
            "                attribute vec2 a_TexCoord;\n" +
            "                varying vec2 v_TexCoord;\n" +
            "                void main() {\n" +
            "                    v_TexCoord = a_TexCoord;\n" +
            "                    gl_Position = a_Position;\n" +
            "                }";

    private final String FRAGMENT_SHADER =  "#extension GL_OES_EGL_image_external : require" +
            " precision mediump float;\n" +
            "                varying vec2 v_TexCoord;\n" +
            "                uniform sampler2D u_TextureUnit;\n" +
            "                void main() {\n" +
            "                    gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);\n" +
            "                }";

    private final int POSITION_COMPONENT_COUNT = 2;

    private float[]  POINT_DATA = new float[]{
            -1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f};

    /**
     * 纹理坐标
     */
    private  float[] TEX_VERTEX =  new float[]{0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f};

    /**
     * 纹理坐标中每个点占的向量个数
     */
    private final int TEX_VERTEX_COMPONENT_COUNT = 2;


    private FloatBuffer mVertexData;
    private FloatBuffer mTexVertexBuffer;

    private int mProgram;

    private int aPositionLocation;
    private int uTextureUnitLocation;

    int textureId;

    SurfaceTexture surfaceTexture;
    private boolean updateSurface= false;
    private boolean isUpdateVideoInfo= true;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;


    public VideoRender(VideoActivity.VideoPlayer videoPlayer, Context context) {
        this.videoPlayer = videoPlayer;
        this.context = context;
        init();
    }

    private void init() {
        mVertexData = BufferUtil.createFloatBuffer(POINT_DATA);
        mTexVertexBuffer = BufferUtil.createFloatBuffer(TEX_VERTEX);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                FRAGMENT_SHADER);

        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
//        连接到着色器程序
        GLES20.glLinkProgram(mProgram);


        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position");
        int aTexCoordLocation = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        uTextureUnitLocation = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        mVertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false, 0, mVertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        // 加载纹理坐标
        mTexVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(aTexCoordLocation, TEX_VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);

//        GLES20.glClearColor(0f, 0f, 0f, 1f);
        // 开启纹理透明混合，这样才能绘制透明图片
//        GLES20.glEnable(GL20.GL_BLEND);
//        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

    }

    private void createTextureId() {
        int [] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);

        if (textureIds[0] == 0) {
            return;
        }
        textureId = textureIds[0];

//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
//
//        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
//        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
//        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
//        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);


        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
//        ShaderUtils.checkGlError("glBindTexture mTextureID");
   /*GLES11Ext.GL_TEXTURE_EXTERNAL_OES的用处？
      之前提到视频解码的输出格式是YUV的（YUV420p，应该是），那么这个扩展纹理的作用就是实现YUV格式到RGB的自动转化，
      我们就不需要再为此写YUV转RGB的代码了*/
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);



        // 创建SurfaceTexture、Surface，并绑定到MediaPlayer上，接收画面驱动回调
        surfaceTexture = new SurfaceTexture(textureIds[0]);
        surfaceTexture.setOnFrameAvailableListener(this);
        Surface surface = new Surface(surfaceTexture);
        videoPlayer.setSurface(surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        updateProjection(width, height);
    }
    private void updateProjection(int videoWidth, int videoHeight) {
        float screenRatio = (float) ScreenUtils.getScreenWidth(context) / ScreenUtils.getScreenHeight(context);
        float videoRatio = (float) videoWidth / videoHeight;
        if (videoRatio > screenRatio) {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -videoRatio / screenRatio, videoRatio / screenRatio, -1f, 1f);
        } else{

            Matrix.orthoM(projectionMatrix, 0, -screenRatio / videoRatio, screenRatio / videoRatio, -1f, 1f, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (updateSurface) {
            // 当有画面帧解析完毕时，驱动SurfaceTexture更新纹理ID到最近一帧解析完的画面，并且驱动底层去解析下一帧画面
            surfaceTexture.updateTexImage();
            updateSurface = false;
        }
        if (isUpdateVideoInfo) {
            updateVertex();
            isUpdateVideoInfo = false;
        }
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, POINT_DATA.length / POSITION_COMPONENT_COUNT);
//        synchronized (this) {
//            if (updateSurface) {
//                surfaceTexture.updateTexImage();//获取新数据
//                surfaceTexture.getTransformMatrix(mSTMatrix);//让新的纹理和纹理坐标系能够正确的对应,mSTMatrix的定义是和projectionMatrix完全一样的。
//                updateSurface = false;
//            }
//        }
//        GLES20.glUseProgram(programId);
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
//        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);

//        vertexBuffer.position(0);
//        GLES20.glEnableVertexAttribArray(aPositionLocation);
//        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false,
//                12, vertexBuffer);
//
//        textureVertexBuffer.position(0);
//        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
//        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);

//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

//        GLES20.glUniform1i(uTextureSamplerLocation, 0);
//        GLES20.glViewport(0, 0, screenWidth, screenHeight);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


    }

    public int loadShader(int type, String shaderCode){
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * 根据视频方向更新顶点坐标
     */
    private void updateVertex() {
        mVertexData = BufferUtil.createFloatBuffer(POINT_DATA);
        mVertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false, 0, mVertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
    }
}
