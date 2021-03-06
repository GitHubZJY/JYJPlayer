package com.jyj.video.jyjplayer.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.event.ToggleLanguageEvent;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.module.setting.language.model.LanguageBean;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * Created by zhengjiayang on 2018/12/2.
 */

public class LanguageUtils {

    public static void updateLanguage(Context context, Locale locale){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        config.locale = locale;
        resources.updateConfiguration(config, dm);
        EventBus.getDefault().post(new ToggleLanguageEvent());
    }

    public static void initLanguageConfig(Context context){
        LanguageBean curLanguage = SpManager.getInstance().getCurLanguage();
        if(curLanguage == null){
            SpManager.getInstance().setCurLanguage(new LanguageBean(Locale.getDefault()));
            updateLanguage(context, Locale.getDefault());
            return;
        }
        updateLanguage(context, curLanguage.getLocale());
    }
}
