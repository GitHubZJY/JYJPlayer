package com.jyj.video.jyjplayer.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * 字体工具体，防止同一字体多次创建
 * Created by chensuilun on 16-9-12.
 */
public class TypefaceUtil {

    private static Typeface sDefaultTypeface = null;

    /**
     * 默认字体
     */
    public static Typeface getDefaultTypeface(Context context) {
        if (sDefaultTypeface == null) {
            sDefaultTypeface = Typeface.createFromAsset(context.getApplicationContext()
                    .getAssets(), "fonts/TitilliumText14L-Bold.otf");
        }
        return sDefaultTypeface;
    }
}
