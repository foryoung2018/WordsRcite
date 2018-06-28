package com.hy.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback
{

    private DrawThread mThread = null;

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

//        mThread = new DrawThread(holder);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
//        mThread.setRun(true);
//        mThread.start();

        if (holder == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);



        // Matrix类进行图片处理（缩小或者旋转）
//        Matrix matrix = new Matrix();
//        // 缩小一倍
//        matrix.postScale(0.5f, 0.5f);
//        // 生成新的图片
//        Bitmap dstbmp = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(),
//                bmp1.getHeight(), matrix, true);
//        // 添加到canvas
//        canvas.drawBitmap(dstbmp, 200, 0, null);




        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + File.separator + "Pictures/5.jpg");  // 获取bitmap
        Canvas canvas = holder.lockCanvas();  // 先锁定当前surfaceView的画布
//        canvas.drawBitmap(bitmap, 0, 0, paint); //执行绘制操作



        Matrix matrix = new Matrix();
        matrix.setScale(640f/bitmap.getWidth(), 480f/bitmap.getHeight());
        //平移到（100，100）处
        canvas.drawBitmap(bitmap,matrix,paint);
        holder.unlockCanvasAndPost(canvas); // 解除锁定并显示在界面上

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
//        mThread.setRun(false);

    }

    /**
     * 绘制线程类
     *
     */
    public class DrawThread extends Thread
    {
        private SurfaceHolder mHolder = null;
        private boolean isRun = false;

        public DrawThread(SurfaceHolder holder)
        {
            mHolder = holder;

        }

        public void setRun(boolean isRun)
        {
            this.isRun = isRun;
        }

        @Override
        public void run()
        {
            int count = 0;

            while (isRun)
            {
                Canvas canvas = null;
                synchronized (mHolder)
                {
                    try
                    {
                        canvas = mHolder.lockCanvas();
                        canvas.drawColor(Color.WHITE);
                        Paint p = new Paint();
                        p.setColor(Color.BLACK);

                        Rect r = new Rect(100, 50, 300, 250);
                        canvas.drawRect(r, p);
                        canvas.drawText("这是第" + (count++) + "秒", 100, 310, p);

                        Thread.sleep(1000);// 睡眠时间为1秒

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                    }
                    finally
                    {
                        if (null != canvas)
                        {
                            mHolder.unlockCanvasAndPost(canvas);
                        }
                    }

                }

            }
        }

    }

}
