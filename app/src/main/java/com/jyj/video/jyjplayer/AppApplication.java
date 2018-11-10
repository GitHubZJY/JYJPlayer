package com.jyj.video.jyjplayer;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    }
}
