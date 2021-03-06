package com.zjyang.base;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.commonsdk.UMConfigure;
import com.zjyang.base.base.SkinManager;
import com.zjyang.base.utils.ScreenUtils;

/**
 * Created by zhengjiayang on 2018/9/26.
 */

public class BaseApp {

    public static Context sContext;

    /**
     * 基础库初始化，必须在主module的application初始化中调用
     * @param context
     */
    public static void init(Context context){
        sContext =context;
        SkinManager.getInstance().init();
        Fresco.initialize(context);
        ScreenUtils.init(context);
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
    }
}
