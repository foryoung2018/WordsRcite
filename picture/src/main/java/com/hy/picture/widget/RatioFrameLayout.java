package com.hy.picture.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.hy.picture.R;


/**
 * Created by Administrator on 2018/6/14.
 */

/**
 * 高度设定，宽度按比例设定
 */
@SuppressLint("AppCompatCustomView")
public class RatioFrameLayout extends FrameLayout {

    private boolean isMeasured;
    private int swidth,sheight;
    private float mRatio;
    private Context context;
    private int width,height;
    private int dpi;
    private boolean withBackground;

    public RatioFrameLayout(@NonNull Context context) {
        super(context);
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        TypedArray tArray = context.obtainStyledAttributes(attrs,
                R.styleable.RatioFrameLayout);

        mRatio = tArray.getFloat(R.styleable.RatioFrameLayout_frameLayoutratio,
                1);
        withBackground = tArray.getBoolean(R.styleable.RatioFrameLayout_frameLayoutwithBackground,
                false);
        tArray.recycle();
        swidth = getScreenWidth(context);
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        dpi = (int) context.getResources().getDimension(R.dimen.t3dp);

    }


    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatio > 0) { // 高度已知，根据比例，设置宽度
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int width =(int) (height * mRatio);

            if(width>swidth){
                width=swidth;
                height=(int)((width+0.f)/mRatio);
            }


            this.width = width;
            this.height = height;
            if(withBackground){
                super.onMeasure(MeasureSpec.makeMeasureSpec(
                        (int) (width), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            }else {

                super.onMeasure(MeasureSpec.makeMeasureSpec(
                        (int) (width), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            }

        }
    }


    public void setRatio(float ratio) {
        this.mRatio = ratio;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     */
    private int getScreenWidth(Context context) {
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return widthPixels;
    }

}
