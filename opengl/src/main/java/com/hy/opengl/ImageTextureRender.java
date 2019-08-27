package com.hy.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageTextureRender implements GLSurfaceView.Renderer {
    private static final String TAG = ImageTextureRender.class.getSimpleName();
    private final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "attribute vec2 vCoordinate;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "\n" +
                    "varying vec2 aCoordinate;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_Position=vMatrix*vPosition;\n" +
                    "    aCoordinate=vCoordinate;\n" +
                    "}";


    private int xLocation = 0;
    private int yLocation = 0;
    private long startTime = 0;

    private final String FRAGMENT_SHADER ="  precision mediump float;\n" +
            "                varying vec2 aCoordinate;\n" +
            "                uniform sampler2D vTexture;\n" +
            "                uniform float xV;\n" +
            "                uniform float yV;\n" +
            "\n" +
            "                vec2 translate(vec2 srcCoord, float x, float y) {\n" +
            "                    if (mod(srcCoord.y, 0.25) > 0.125) {\n" +
            "                        return vec2(srcCoord.x + x, srcCoord.y + y);\n" +
            "                    } else {\n" +
            "                        return vec2(srcCoord.x - x, srcCoord.y + y);\n" +
            "                    }\n" +
            "                }\n" +
            "\n" +
            "                void main() {\n" +
            "                    vec2 offsetTexCoord = translate(aCoordinate, xV, yV);\n" +
            "\n" +
            "                    if (offsetTexCoord.x >= 0.0 && offsetTexCoord.x <= 1.0 &&\n" +
            "                        offsetTexCoord.y >= 0.0 && offsetTexCoord.y <= 1.0) {\n" +
            "                        gl_FragColor = texture2D(vTexture, offsetTexCoord);\n" +
            "                    }\n" +
            "                }";

    private final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "\n" +
                    "uniform sampler2D aCoordinate;\n" +
                    "uniform int vChangeType;\n" +
                    "uniform vec3 vChangeColor;\n" +
                    "\n" +
                    "varying vec2 aCoordinate;\n" +
                    "\n" +
                    "void modifyColor(vec4 color){\n" +
                    "    color.r=max(min(color.r,1.0),0.0);\n" +
                    "    color.g=max(min(color.g,1.0),0.0);\n" +
                    "    color.b=max(min(color.b,1.0),0.0);\n" +
                    "    color.a=max(min(color.a,1.0),0.0);\n" +
                    "}\n" +
                    "\n" +
                    "void main(){\n" +
                    "    vec4 nColor=texture2D(aCoordinate,aCoordinate);\n" +
                    "   if(vChangeType==1){\n" +
                    "        float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;\n" +
                    "        gl_FragColor=vec4(c,c,c,nColor.a);\n" +
                    "    }else if(vChangeType==2){\n" +
                    "        vec4 deltaColor=nColor+vec4(vChangeColor,0.0);\n" +
                    "        modifyColor(deltaColor);\n" +
                    "        gl_FragColor=deltaColor;\n" +
                    "    }else{\n" +
                    "        gl_FragColor=nColor;\n" +
                    "    }\n" +
                    "}";

    private final Context context;
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    private final float[] sPos={
            -1.0f,1.0f,    //左上角
            -1.0f,-1.0f,   //左下角
            1.0f,1.0f,     //右上角
            1.0f,-1.0f     //右下角
    };
    private final float[] ssPos={
            -1.0f,1.0f,    //左上角
            -1.0f,-1.0f,   //左下角
            1.0f,1.0f,     //右上角
            1.0f,-1.0f     //右下角
    };


    private int mProgram;

    private final float[] tex_vertex ={
//            0.0f,0.0f,
//            0.0f,1.0f,
//            1.0f,0.0f,
//            1.0f,1.0f,

            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f
    };


    private float[]  POINT_DATA2 = {-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f};

    private float[] pint_data = {-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f};


    Bitmap mBitmap;
    Bitmap mBitmap1;
    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int glHMatrix;
    private int hIsHalf;
    private int glHUxy;

    private FloatBuffer verBuffer;
    private FloatBuffer aPos;
    private FloatBuffer pointBuffer;

    public ImageTextureRender(Context context) {
        this.context = context;
        mBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.fengj);
        mBitmap1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.fengj);
    }

    public int loadShader(int type, String shaderCode){
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                FRAGMENT_SHADER);

        ByteBuffer bb=ByteBuffer.allocateDirect(tex_vertex.length*4);
        bb.order(ByteOrder.nativeOrder());
        verBuffer =bb.asFloatBuffer();
        verBuffer.put(tex_vertex);
        verBuffer.position(0);
        ByteBuffer aa=ByteBuffer.allocateDirect(pint_data.length*4);
        aa.order(ByteOrder.nativeOrder());
        aPos=aa.asFloatBuffer();
        aPos.put(pint_data);
        aPos.position(0);
        ByteBuffer cc=ByteBuffer.allocateDirect(pint_data.length*4);
        cc.order(ByteOrder.nativeOrder());
        pointBuffer =cc.asFloatBuffer();
        pointBuffer.put(pint_data);
        pointBuffer.position(0);


//        创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
//        连接到着色器程序
        GLES20.glLinkProgram(mProgram);
//        mProgram=ShaderUtils.createProgram(context.getResources(),vertex,fragment);
        glHPosition=GLES20.glGetAttribLocation(mProgram,"vPosition");
        glHCoordinate=GLES20.glGetAttribLocation(mProgram,"vCoordinate");
        glHTexture=GLES20.glGetUniformLocation(mProgram,"aCoordinate");
        glHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
//        hIsHalf=GLES20.glGetUniformLocation(mProgram,"vIsHalf");
//        glHUxy=GLES20.glGetUniformLocation(mProgram,"uXY");


        onDrawCreatedSet(mProgram);
        GLES20.glVertexAttribPointer(glHPosition,2,GLES20.GL_FLOAT,false,0, pointBuffer);
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glVertexAttribPointer(glHCoordinate,2,GLES20.GL_FLOAT,false,0, verBuffer);
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        //传入顶点坐标

        GLES20.glEnable(GL10.GL_BLEND);
        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void onDrawCreatedSet(int mProgram) {
//        hChangeType=GLES20.glGetUniformLocation(mProgram,"vChangeType");
//        hChangeColor=GLES20.glGetUniformLocation(mProgram,"vChangeColor");
        startTime = System.currentTimeMillis();
        xLocation = GLES20.glGetUniformLocation(mProgram,"xV");
        yLocation = GLES20.glGetUniformLocation(mProgram,"yV");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        GLES20.glViewport(0,0,width,height);

        int w=mBitmap.getWidth();
        int h=mBitmap.getHeight();
        float sWH=w/(float)h;
        float sWidthHeight=width/(float)height;
        if(width>height){
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 7);
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

    }

    private int textureId;
    private int textureId1;
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
//        onDrawSet();
        GLES20.glUniformMatrix4fv(glHMatrix,1,false,mMVPMatrix,0);

//        textureId1=createTexture1();
        textureId=createTexture();
        double intensity = Math.sin((System.currentTimeMillis() - startTime) / 1000.0) * 0.5;
        Log.d(TAG, "onDrawFrame() called with: intensity = [" + intensity + "]");
        GLES20.glUniform1f(xLocation, (float) intensity);
        GLES20.glUniform1f(yLocation, 0.0f);

    }



    private int hChangeType;
    private int hChangeColor;

    //    NONE(0,new float[]{0.0f,0.0f,0.0f}),
//    GRAY(1,new float[]{0.299f,0.587f,0.114f}),
//    COOL(2,new float[]{0.0f,0.0f,0.1f}),
//    WARM(2,new float[]{0.1f,0.1f,0.0f}),
//    BLUR(3,new float[]{0.006f,0.004f,0.002f}),
//    MAGN(4,new float[]{0.0f,0.0f,0.4f});
    public void onDrawSet() {
        GLES20.glUniform1i(hChangeType,3);
        GLES20.glUniform3fv(hChangeColor,1,new float[]{0.006f,0.004f,0.002f},0);
    }

    private int createTexture(){
        int[] texture=new int[1];
        if(mBitmap!=null&&!mBitmap.isRecycled()){

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

            GLES20.glEnableVertexAttribArray(glHCoordinate);
            //传入纹理坐标
//            GLES20.glVertexAttribPointer(glHCoordinate,2,GLES20.GL_FLOAT,false,0,pointBuffer);
            GLES20.glUniform1i(glHTexture, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,4);
            return texture[0];
        }
        return 0;
    }
    private int createTexture1(){
        int[] texture=new int[1];
        if(mBitmap1!=null&&!mBitmap1.isRecycled()){
            aPos.position(0);
            //传入顶点坐标
            GLES20.glVertexAttribPointer(glHPosition,2,GLES20.GL_FLOAT,false,0,aPos);
            GLES20.glEnableVertexAttribArray(glHPosition);
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap1, 0);

            GLES20.glEnableVertexAttribArray(glHCoordinate);
            //传入纹理坐标
//            GLES20.glVertexAttribPointer(glHCoordinate,2,GLES20.GL_FLOAT,false,0,pointBuffer);
            GLES20.glUniform1i(glHTexture, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
            return texture[0];
        }
        return 1;
    }
}
