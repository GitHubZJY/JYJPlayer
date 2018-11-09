package com.example.zjy.player.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by 74215 on 2018/3/28.
 */

public class ScreenUtils {


    /**
     * 获取横竖屏方向
     * @param context
     * @return
     */
    public static int getScreenOrientation(Context context){
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return Configuration.ORIENTATION_LANDSCAPE; // 横屏
        } else  {
            return Configuration.ORIENTATION_PORTRAIT; // 竖屏
        }
    }

    /**
     * 获取状态栏高度
     * */
    public static int getStatusBarHeight(Context context){

        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
