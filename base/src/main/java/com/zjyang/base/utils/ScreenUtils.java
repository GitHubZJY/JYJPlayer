package com.zjyang.base.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.zjyang.base.BaseApp;


/**
 * Created by 74215 on 2018/3/10.
 */

public class ScreenUtils {

    private static int sScreenWidth;
    private static int sScreenHeight;

    /**
     * 获取手机的屏幕的密度
     *
     * @param context
     */
    public static void init(Context context) {
        if (context != null) {
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();
            sScreenWidth = displayMetrics.widthPixels;
            sScreenHeight = displayMetrics.heightPixels;
        }
    }

    public static int getsScreenWidth() {
        return sScreenWidth;
    }

    public static int getsScreenHeight() {
        return sScreenHeight;
    }

    /**
     * 获取状态栏高度
     * */
    public static int getStatusBarHeight(){

        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = BaseApp.sContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = BaseApp.sContext.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
