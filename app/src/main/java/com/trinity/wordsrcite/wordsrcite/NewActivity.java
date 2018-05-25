package com.trinity.wordsrcite.wordsrcite;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.trinity.wordsrcite.wordsrcite.dialog.POperationDialog;
import com.trinity.wordsrcite.wordsrcite.fragment.TranslateFragment;
import com.trinity.wordsrcite.wordsrcite.view.BottomBar;
import com.trinity.wordsrcite.wordsrcite.view.BottomBarTab;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity
//        implements RadioGroup.OnCheckedChangeListener
{

    private RadioGroup mRadioGroup;
    private static List<Fragment> fragments = new ArrayList<>();
    private static Fragment fragment;
    private FragmentManager fm;
    private static FragmentTransaction transaction;
    private RadioButton rb_Home,rb_Message,rb_Find,rb_My;

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        fragments = getFragments(); //添加布局
        //添加默认布局
        normalFragment();
        initBottom();

    }



    public static class Listener implements  BottomBar.OnTabSelectedListener{
        private final FragmentManager fm;
        private NewActivity context;
        private static FragmentTransaction transaction;

        public Listener(NewActivity newActivity) {
            context = newActivity;
            fm=context.getSupportFragmentManager();
            transaction=fm.beginTransaction();
        }

        @Override
        public void onTabSelected(int position, int prePosition) {
            Log.i("11","onTabSelected :position  " + position +"prePosition" + position );
            Fragment fragment=fragments.get(position);
            transaction=fm.beginTransaction();
            transaction.replace(R.id.mFragment,fragment);
            transaction.commit();
        }

        @Override
        public void onTabUnselected(int position) {

        }

        @Override
        public void onTabReselected(int position) {

        }
    }

    private void initBottom() {
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);

        mBottomBar.addItem(new BottomBarTab(this, R.drawable.word_rb))
                .addItem(new BottomBarTab(this, R.drawable.translate))
                .addItem(new BottomBarTab(this, R.drawable.ic_message_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_account_circle_white_24dp));

        mBottomBar.setOnTabSelectedListener(new Listener(this));
    }

    //默认布局
    private void normalFragment() {
        fm=getSupportFragmentManager();
        transaction=fm.beginTransaction();
        fragment=fragments.get(0);
        transaction.replace(R.id.mFragment,fragment);
        transaction.commit();
    }


    public List<Fragment> getFragments() {
        fragments.add(new HomeFragment());
        fragments.add(new TranslateFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        return fragments;
    }
}
