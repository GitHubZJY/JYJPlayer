package com.zjyang.base.utils;

import android.util.Log;

import com.zjyang.base.BuildConfig;


/**
 * Created by zhengjiayang on 2018/3/1.
 */

public class LogUtil {

    private static final String TAG = LogUtil.class.getSimpleName();

    private static final boolean IS_DEBUG = BuildConfig.DEBUG;

    public static void e(String tag, String msg) {
        if (IS_DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (IS_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (IS_DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (IS_DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (IS_DEBUG) {
            Log.v(tag, msg);
        }
    }


}
