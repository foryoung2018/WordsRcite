//package com.hy.opengl;
//
//import android.content.Context;
//import android.opengl.GLES20;
//
//import javax.microedition.khronos.opengles.GL10;
//
//public class BaseFilter {
//
//    private String vertexShaderCode =
//            "attribute vec4 vPosition;\n" +
//                    "attribute vec2 vCoordinate;\n" +
//                    "uniform mat4 vMatrix;\n" +
//                    "\n" +
//                    "varying vec2 aCoordinate;\n" +
//                    "\n" +
//                    "void main(){\n" +
//                    "    gl_Position=vMatrix*vPosition;\n" +
//                    "    aCoordinate=vCoordinate;\n" +
//                    "}";
//
//    private  String fragmentShaderCode =
//            "precision mediump float;\n" +
//                    "\n" +
//                    "uniform sampler2D vTexture;\n" +
//                    "uniform int vChangeType;\n" +
//                    "uniform vec3 vChangeColor;\n" +
//                    "\n" +
//                    "varying vec2 aCoordinate;\n" +
//                    "\n" +
//                    "void modifyColor(vec4 color){\n" +
//                    "    color.r=max(min(color.r,1.0),0.0);\n" +
//                    "    color.g=max(min(color.g,1.0),0.0);\n" +
//                    "    color.b=max(min(color.b,1.0),0.0);\n" +
//                    "    color.a=max(min(color.a,1.0),0.0);\n" +
//                    "}\n" +
//                    "\n" +
//                    "void main(){\n" +
//                    "    vec4 nColor=texture2D(vTexture,aCoordinate);\n" +
//                    "   if(vChangeType==1){\n" +
//                    "        float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;\n" +
//                    "        gl_FragColor=vec4(c,c,c,nColor.a);\n" +
//                    "    }else if(vChangeType==2){\n" +
//                    "        vec4 deltaColor=nColor+vec4(vChangeColor,0.0);\n" +
//                    "        modifyColor(deltaColor);\n" +
//                    "        gl_FragColor=deltaColor;\n" +
//                    "    }else{\n" +
//                    "        gl_FragColor=nColor;\n" +
//                    "    }\n" +
//                    "}";
//
//    private Context context;
//
//    public BaseFilter(String vertexShaderCode, String fragmentShaderCode, Context context) {
//        this.vertexShaderCode = vertexShaderCode;
//        this.fragmentShaderCode = fragmentShaderCode;
//        this.context = context;
//
//        oncreate();
//    }
//
//    public void oncreate() {
//
//        makeProgram(vertexShader, fragmentShader);
//        val aPositionLocation = getAttrib("a_Position");
//        projectionMatrixHelper = ProjectionMatrixHelper(program, "u_Matrix");
//        // 纹理坐标索引
//        val aTexCoordLocation = getAttrib("a_TexCoord");
//        uTextureUnitLocation = getUniform("u_TextureUnit");
//
//        mVertexData.position(0)
//        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
//                GLES20.GL_FLOAT, false, 0, mVertexData)
//        GLES20.glEnableVertexAttribArray(aPositionLocation);
//
//        // 加载纹理坐标
//        mTexVertexBuffer.position(0)
//        GLES20.glVertexAttribPointer(aTexCoordLocation, TEX_VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
//        GLES20.glEnableVertexAttribArray(aTexCoordLocation);
//
//        GLES20.glClearColor(0f, 0f, 0f, 1f)
//        // 开启纹理透明混合，这样才能绘制透明图片
//        GLES20.glEnable(GL10.GL_BLEND)
//        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
//
//    }
//}
