package com.jyj.video.jyjplayer;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jyj.video.jyjplayer.utils.LanguageUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.zjyang.base.BaseApp;

import tv.danmaku.ijk.media.player.PlugInSoHelper;

/**
 * Created by 74215 on 2018/11/1.
 */

public class AppApplication extends Application{

    private static Context sContext;
    private static Gson sGson;

    public static Gson getGson() {
        if (sGson == null) {
            synchronized (new Object()) {
                if (sGson == null) {
                    sGson = new GsonBuilder().setPrettyPrinting()
                            .disableHtmlEscaping()
                            .serializeSpecialFloatingPointValues()
                            .create();
                }
            }
        }
        return sGson;
    }

    public static Context getContext() {
        return sContext.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getContext();
        BaseApp.init(this);
        //复制加载ijk so库
        new PlugInSoHelper(this).run();
        FileDownloader.setupOnApplicationOnCreate(this);
        LanguageUtils.initLanguageConfig(this);
    }

    /**
     * 获取版本号名称
     *
     * @return
     */
    public static String getVersionName() {
        String verName = "";
        try {
            verName = sContext.getPackageManager().
                    getPackageInfo(sContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
