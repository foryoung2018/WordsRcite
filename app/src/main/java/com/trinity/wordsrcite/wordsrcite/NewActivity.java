package com.trinity.wordsrcite.wordsrcite;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mRadioGroup;
    private List<Fragment> fragments = new ArrayList<>();
    private Fragment fragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private RadioButton rb_Home,rb_Message,rb_Find,rb_My;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        initView(); //初始化组件
        mRadioGroup.setOnCheckedChangeListener(this); //点击事件
        fragments = getFragments(); //添加布局
        //添加默认布局
        normalFragment();
    }

    //默认布局
    private void normalFragment() {
        fm=getSupportFragmentManager();
        transaction=fm.beginTransaction();
        fragment=fragments.get(0);
        transaction.replace(R.id.mFragment,fragment);
        transaction.commit();
    }

    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        rb_Home= (RadioButton) findViewById(R.id.mRb_home);
        rb_Message= (RadioButton) findViewById(R.id.mRb_message);
        rb_Find= (RadioButton) findViewById(R.id.mRb_find);
        rb_My= (RadioButton) findViewById(R.id.mRb_my);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        fm=getSupportFragmentManager();
        transaction=fm.beginTransaction();
        switch (checkedId){
            case R.id.mRb_home:
                fragment=fragments.get(0);
                transaction.replace(R.id.mFragment,fragment);
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mRb_message:
                fragment=fragments.get(1);
                transaction.replace(R.id.mFragment,fragment);
                Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mRb_find:
                fragment=fragments.get(2);
                transaction.replace(R.id.mFragment,fragment);
                Toast.makeText(this, "Find", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mRb_my:
                fragment=fragments.get(3);
                transaction.replace(R.id.mFragment,fragment);
                Toast.makeText(this, "My", Toast.LENGTH_SHORT).show();
                break;
        }
        setTabState();
        transaction.commit();
    }

    //设置选中和未选择的状态
    private void setTabState() {
        setHomeState();
        setMessageState();
        setFindState();
        setMyState();
    }

    private void setMyState() {
        if (rb_My.isChecked()){
            rb_My.setTextColor(ContextCompat.getColor(this,R.color.black));
        }else{
            rb_My.setTextColor(ContextCompat.getColor(this,R.color.white));
        }
    }

    private void setFindState() {
        if (rb_Find.isChecked()){
            rb_Find.setTextColor(ContextCompat.getColor(this,R.color.black));
        }else{
            rb_Find.setTextColor(ContextCompat.getColor(this,R.color.white));
        }
    }

    private void setMessageState() {
        if (rb_Message.isChecked()){
            rb_Message.setTextColor(ContextCompat.getColor(this,R.color.black));
        }else{
            rb_Message.setTextColor(ContextCompat.getColor(this,R.color.white));
        }
    }

    private void setHomeState() {
        if (rb_Home.isChecked()){
            rb_Home.setTextColor(ContextCompat.getColor(this,R.color.black));
        }else{
            rb_Home.setTextColor(ContextCompat.getColor(this,R.color.white));
        }
    }

    public List<Fragment> getFragments() {
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        return fragments;
    }
}
