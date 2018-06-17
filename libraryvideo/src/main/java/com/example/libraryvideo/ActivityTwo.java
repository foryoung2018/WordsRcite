package com.example.libraryvideo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.example.libraryvideo.otherplay.AVPlayer;
import com.example.libraryvideo.surface.GlView;
import com.example.libraryvideo.surface.MediaController;

import java.io.File;

import static com.example.libraryvideo.BtnAction.ACCELE;
import static com.example.libraryvideo.BtnAction.ATIMESLOW;
import static com.example.libraryvideo.BtnAction.PLAY;
import static com.example.libraryvideo.BtnAction.SLOW;
import static com.example.libraryvideo.BtnAction.STOP;
import static com.example.libraryvideo.BtnAction.VTIMESLOW;


enum BtnAction{
        PLAY, STOP, ACCELE, SLOW, VTIMESLOW, ATIMESLOW;
};

public class ActivityTwo extends Activity {
    //真是他妹的烦android这种布局，明明可以循环解决的问题非的要一个一个的做
    AVPlayer avPlayer;
    String input;
    GlView glView;
    MainViewAdapter   adapter;
    Button btnPlay;
    Button btnStop;
    Button btnAccele;
    Button btnSlow;
    Button btnVTimeSlow;
    Button btnATimeSlow;
    private ProgressBar mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glView = (GlView) findViewById(R.id.glView);
        btnPlay= (Button) findViewById(R.id.btn_play);
        btnStop= (Button) findViewById(R.id.btn_stop);
        btnSlow= (Button) findViewById(R.id.btn_slow);
        btnAccele= (Button) findViewById(R.id.btn_accelerate);
        btnATimeSlow= (Button) findViewById(R.id.btn_acce_video);
        btnVTimeSlow= (Button) findViewById(R.id.btn_acce_audio);
        mProgress = (SeekBar) findViewById(R.id.mediacontroller_seekbar);
        AddBtnAction();
        avPlayer = new AVPlayer();
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


//    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
//        public void onStartTrackingTouch(SeekBar bar) {
//            mDragging = true;
//            show(3600000);
//            mHandler.removeMessages(SHOW_PROGRESS);
//            if (mInstantSeeking)
//                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
//            if (mInfoView != null) {
//                mInfoView.setText("");
//                mInfoView.setVisibility(View.VISIBLE);
//            }
//        }
//
//        public void onProgressChanged(SeekBar bar, int progress,
//                                      boolean fromuser) {
//            if (!fromuser)
//                return;
//
//            final long newposition = (mDuration * progress) / 1000;
//            String time = generateTime(newposition);
//            if (mInstantSeeking) {
//                mHandler.removeCallbacks(lastRunnable);
//                lastRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        mPlayer.seekTo(newposition);
//                    }
//                };
//                mHandler.postDelayed(lastRunnable, 200);
//            }
//            if (mInfoView != null)
//                mInfoView.setText(time);
//            if (mCurrentTime != null)
//                mCurrentTime.setText(time);
//        }
//
//        public void onStopTrackingTouch(SeekBar bar) {
//            if (!mInstantSeeking)
//                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
//            if (mInfoView != null) {
//                mInfoView.setText("");
//                mInfoView.setVisibility(View.GONE);
//            }
//            show(sDefaultTimeout);
//            mHandler.removeMessages(SHOW_PROGRESS);
//            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
//            mDragging = false;
//            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
//        }
//    };



    @Override
    protected void onResume() {
        super.onResume();
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(ActivityTwo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    ActivityTwo.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    void BtnAction(BtnAction action){
        switch (action){
            case PLAY:
                {
//                    String liveInput = "http://221.228.226.23/11/t/j/v/b/tjvbwspwhqdmgouolposcsfafpedmb/sh.yinyuetai.com/691201536EE4912BF7E4F1E2C67B8119.mp4";
                   // String liveInput = "http://flv2.bn.netease.com/videolib3/1604/28/fVobI0704/SD/fVobI0704-mobile.mp4";
                   // String liveInput = "http://221.228.226.5/15/t/s/h/v/tshvhsxwkbjlipfohhamjkraxuknsc/sh.yinyuetai.com/88DC015DB03C829C2126EEBBB5A887CB.mp4";
                    String liveInput = new File(Environment.getExternalStorageDirectory(),"video.mp4").getAbsolutePath();
                    //  Toast.makeText(this,input,Toast.LENGTH_SHORT).show();
                    int ret =  avPlayer.OpenAVWithUrlAndView(liveInput,null,glView);

                    final Handler handler=new Handler();


                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Log.i("*** yang ","duration" + avPlayer.getDuration()/1000);
                            avPlayer.progress(mProgress);
                            handler.postDelayed(this, 1000);
                        }
                    };

                    Runnable runnable1=new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
//                            AVPlayer.seekTo();
                        }
                    };



                    handler.postDelayed(runnable, 1000);
                    handler.postDelayed(runnable1, 2000);
                }
                break;
            case STOP:
                {

                }
                break;
            case ACCELE:
                {
                    avPlayer.setPlayRate(1);
                }
                break;
            case SLOW:
                {
                    avPlayer.setPlayRate(-1);
                }
                break;
            case ATIMESLOW:
                {//单位毫秒
                    avPlayer.setPlayAudioOrVideoRate(100);
                }
                break;
            case VTIMESLOW:
                {
                    avPlayer.setPlayAudioOrVideoRate(-100);
                }
                break;
                default:
                break;
        }
    }

    void AddBtnAction(){
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnAction(PLAY);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnAction(STOP);
            }
        });
        btnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnAction(SLOW);
            }
        });
        btnAccele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnAction(ACCELE);
            }
        });
        btnVTimeSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnAction(VTIMESLOW);
            }
        });
        btnATimeSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnAction(ATIMESLOW);
            }
        });
    }
}
