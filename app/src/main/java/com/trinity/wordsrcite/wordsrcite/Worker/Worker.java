package com.trinity.wordsrcite.wordsrcite.Worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Worker extends ThreadPoolExecutor {

    public Worker( int corePoolSize,
                   int maximumPoolSize,
                   long keepAliveTime,
                   TimeUnit unit,
                   BlockingQueue<Runnable> workQueue )      {

        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue) ;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r) ;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t ) {
        super.afterExecute(r, t) ;
    }

}