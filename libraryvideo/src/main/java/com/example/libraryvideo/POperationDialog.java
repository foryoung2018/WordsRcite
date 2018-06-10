package com.example.libraryvideo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

public class POperationDialog<T> extends AlertDialog {

    private ViewPager m_viewPager;
    private List<T> list;
    private List<View> views;
    private Context mContext;
    private OperatePagerAdapter adapter;
    private ImageView imageCancel;


    public POperationDialog(@NonNull Context context, List<T> list) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.list = list;
    }

    private void setData() {
        views = new LinkedList<>();
        for(T t : list){
            View view = LayoutInflater.from(mContext).inflate(R.layout.p_item_dialog_operation,null,false);
            view.setBackgroundColor(Color.parseColor("#00000000"));
            views.add(view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_dialog_operation);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = (int) (display.getWidth());
        lp.height = (int) (display.getHeight() * 0.65);

        dialogWindow.setAttributes(lp);

        WindowManager.LayoutParams lp1 = ((Activity)mContext).getWindow().getAttributes();
//        lp1.alpha=0.2f;
        ((Activity)mContext).getWindow().setAttributes(lp1);
        setData();
        initView();
    }

    private void initView() {
        m_viewPager = (ViewPager) findViewById(R.id.pager);
        imageCancel  = (ImageView) findViewById(R.id.image_cancel);
//        btCancel.setLayoutParams(param);
        adapter = new OperatePagerAdapter(views);
        m_viewPager.setOffscreenPageLimit(3);
        m_viewPager.setPageMargin(10);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                screenWidth * 3/4, screenHeight*3/5);
        m_viewPager.setLayoutParams(params);
        m_viewPager.setAdapter(adapter);
//        m_viewPager.setPageTransformer(true, new ScrollOffsetTransformer());
        m_viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POperationDialog.this.dismiss();
            }
        });
    }


    class OperatePagerAdapter extends PagerAdapter {
        private List<View> mList;

        public OperatePagerAdapter(List<View> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position),0);
            return mList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    public static class ScrollOffsetTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 1f;
        //主要是设置在不同位置上的VIEW的活动动画
        @Override
        public void transformPage(View view, float position) {

        }
    }


}
