package com.zjyang.base.utils;

import android.content.Context;
import android.util.TypedValue;

import com.zjyang.base.BaseApp;

/**
 * Created by 74215 on 2018/4/16.
 */

public class DrawUtils {


    /**
     * dp转px
     * @param dpVal   dp value
     * @return px value
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                BaseApp.sContext.getResources().getDisplayMetrics());
    }

    /**
     * sp 转 px
     *
     * @param spValue
     *            sp大小
     * @return 像素值
     */
    public static int sp2px(float spValue) {
        final float scale = BaseApp.sContext.getResources().getDisplayMetrics().density;
        return (int) (scale * spValue);
    }
}
