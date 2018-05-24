package com.trinity.wordsrcite.wordsrcite.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.trinity.wordsrcite.wordsrcite.R;

import java.util.ArrayList;
import java.util.List;

public class POperationDialog<T> extends AlertDialog {

    private ViewPager m_viewPager;
    private List<T> list;
    private List<View> views;
    private Context mContext;
    private OperatePagerAdapter adapter;


    public POperationDialog(@NonNull Context context, List<T> list) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.list = list;
    }

    private void setData() {
        views = new ArrayList<>();
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
        setData();
        initView();
    }

    private void initView() {
        m_viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new OperatePagerAdapter(views);
        m_viewPager.setAdapter(adapter);
        m_viewPager.setPageTransformer(true, new ScrollOffsetTransformer());
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
    }


    class OperatePagerAdapter extends PagerAdapter {
        private List<View> mList;
        private List<ViewGroup> views;

        public OperatePagerAdapter(List<View> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position));
            return mList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(mList.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    public static class ScrollOffsetTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            Log.i("yang","position" + position);
//            if (position == 1) {
//                右侧的缓存页往左偏移100
//                page.setTranslationX(-180 * position);
//            }

//            else{
//                page.setTranslationX(150 * position);
//            }
        }
    }


}
