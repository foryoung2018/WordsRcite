package com.trinity.wordsrcite.wordsrcite.Worker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.trinity.wordsrcite.wordsrcite.MyApplication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkUtil {

    static Worker worker;
    static BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(5) ;

    public static void init(){
        if (worker==null){
            worker = new Worker(2, 5, 100, TimeUnit.SECONDS, blockingQueue) ;
        }
    }

    public static void excute(Runnable r , Handler handler, int arg){

        init();
        worker.execute(r);
        Message msg = Message.obtain();
        msg.what = arg;
        handler.sendMessage(msg);

//        dialog.dismiss();
    }



}
