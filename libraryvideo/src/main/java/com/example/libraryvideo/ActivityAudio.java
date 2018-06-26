package com.example.libraryvideo;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

// @author foryoung
// @date   2018/6/26
// @desc   opensles 音频播放

@Route(path = "/libraryvideo/audio")
public class ActivityAudio extends Activity {

    static {
        System.loadLibrary("openslaudio");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
    }

    public native void playAudioByOpenSL_assets(AssetManager assetManager, String filename);

    public native void playAudioByOpenSL_pcm(String pamPath);

    public void play_assets(View view) {
        AssetManager assetManager = getAssets();
        playAudioByOpenSL_assets(assetManager, "mydream.m4a");
    }

    public void play_pcm(View view) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydream.pcm";
        Log.d("ywl5320", path);
        playAudioByOpenSL_pcm(path);
    }
}
