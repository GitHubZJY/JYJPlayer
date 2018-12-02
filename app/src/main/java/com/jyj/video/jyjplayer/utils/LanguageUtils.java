package com.jyj.video.jyjplayer.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.jyj.video.jyjplayer.AppApplication;

import java.util.Locale;

/**
 * Created by zhengjiayang on 2018/12/2.
 */

public class LanguageUtils {

    public static void updateLanguage(){
        Resources resources = AppApplication.getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        config.locale = Locale.ENGLISH;
        resources.updateConfiguration(config, dm);
    }
}
