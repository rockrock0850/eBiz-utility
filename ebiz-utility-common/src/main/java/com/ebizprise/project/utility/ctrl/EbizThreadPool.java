package com.ebizprise.project.utility.ctrl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Thread Pool
 * 
 * The <code>EbizThreadPool</code>  
 * 
 * @author andrew.lee
 * @version 1.0, Created at 2020年4月17日
 */
public class EbizThreadPool {

    @SuppressWarnings("rawtypes")
    public static ThreadPoolExecutor getThreadPool(){
        // 根據底層硬體來決定數量
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maxPoolSize = corePoolSize*2;
        long keepAliveTime = 60l;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        BlockingQueue workQueue = new LinkedBlockingQueue<>(75);
        // 对拒绝任务不抛弃，而是抛弃队列里面等待最久的一个线程，然后把拒绝任务加到队列
        RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        //
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, workQueue, rejectHandler);
        // 可以在Core Thread閒置的時候, 讓系統回收
        threadPool.allowCoreThreadTimeOut(true);
        // 一開始Thread Pool剛建立Thread, 此時並沒有任何Thread可以執行Task,
        // 因此所有的Task將會被丟進Queue內等待, 直到有新的Task後來又被加入,
        // 才會連同之前等待的Task一起執行。
        // 如果要解決這個問題可以預先建立core thread。
        threadPool.prestartCoreThread();
        
        return threadPool;
    }
}
