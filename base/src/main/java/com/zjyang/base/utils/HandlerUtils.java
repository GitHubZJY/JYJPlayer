package com.zjyang.base.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by zhengjiayang on 2018/3/2.
 */

public class HandlerUtils {

    private static HandlerThread sWorkerThread = new HandlerThread("flashlight-main");

    static {
        sWorkerThread.start();
    }

    private static Handler sWorker = new Handler(sWorkerThread.getLooper());

    private static Handler sHandler = new Handler(Looper.getMainLooper());



    /**
     * 向线程发送数据
     *
     * @param r runnable 对象
     */
    public static void postThread(Runnable r) {
        sWorker.post(r);
    }

    /**
     * 向线程发送数据
     *
     * @param r runnable 对象
     */
    public static void removeThread(Runnable r) {
        sWorker.removeCallbacks(r);
    }

    public static void postThreadDelayed(Runnable r, long delayMillis) {
        sWorker.postDelayed(r, delayMillis);
    }

    /**
     * 向主线程发送任务
     *
     */
    public static void post(Runnable runnable) {
        sHandler.post(runnable);
    }

    /**
     * 主线程
     *
     */
    public static void remove(Runnable runnable) {
        sHandler.removeCallbacks(runnable);
    }

    /**
     * 主线程
     *
     */
    public static void postDelay(Runnable runnable, long delay) {
        sHandler.postDelayed(runnable, delay);
    }



}
