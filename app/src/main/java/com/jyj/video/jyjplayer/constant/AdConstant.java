package com.jyj.video.jyjplayer.constant;

/**
 * @author denglongyun
 * @date 17-3-15
 */

public interface AdConstant {

    /**
     * 广告类型
     * @author denglongyun
     */
    interface AdType {
        int AD_TYPE_BRING = 0; // 带量素材
        int AD_TYPE_FB_NATIVE = 1;
        int AD_TYPE_OFF_ON_LINE = 2;
        int AD_TYPE_ADMOB_INTERSTITIAL = 3;
        int AD_TYPE_MOPUB_BANNER = 4; // mopub banner不能拦截touch事件
        int AD_TYPE_MOPUB_NATIVE = 5;
        int AD_TYPE_MOPUB_INTERSTITIAL = 6; //mopub全屏
        int AD_TYPE_FB_INTERSTITIAL = 7; //FB全屏

        int AD_TYPE_ADMOB_NATIVE_CONTENT = 8;   //admob native 广告
        int AD_TYPE_ADMOB_NATIVE_INSTALL = 9;   //admob native 广告
        int AD_TYPE_ADMOB_BANNER = 10;   //Admob:Banner广告
        int AD_TYPE_FB_BANNER = 11;   //fb:Banner广告
        int AD_TYPE_MOPUB_BANNER_GOMO = 12; // 广告sdk封装过的banner广告
    }

    interface ScreenOnAd {
        /**
         * 解锁后持续时间，超过这个时间不展示广告（两个时机进行记录，解锁且没工具锁屏和工具锁屏关闭且没解锁）
         */
        int DEFAULT_PERSIST_TIME = 5 * (int) TimeConstant.ONE_MIN;

        boolean DEFAULT_ACTIVE = true;
        int DEFAULT_START_TIME = 0; // 展示开始时间 min
        int DEFAULT_END_TIME = 0; // 展示结束时间 min
        int DEFAULT_SHOW_TIMES_ADAY = 0;    // 一天展示5次
        int DEFAULT_SHOW_TIME_SPLIT = 60;    // 展示间隔
        int DEFAULT_FB_CLICK_MODEL = 2;    // 整形枚举 0-全局可点／1-局部可点／2-仅download可点（默认2）
    }

    interface Pause {

        boolean DEFAULT_ACTIVE = true;
        int DEFAULT_SHOW_TIMES_ADAY = 0;    // 一天展示0次
        int DEFAULT_SHOW_TIME_SPLIT = 30;    // 展示间隔30min
        int DEFAULT_FB_CLICK_MODEL = 2;    // 整形枚举 0-全局可点／1-局部可点／2-仅download可点（默认2）
    }

    interface HomeKey {
        int DEFAULT_START_TIME = 0; // 展示开始时间 min
        int DEFAULT_END_TIME = 0; // 展示结束时间 min
        int DEFAULT_SHOW_TIMES_ADAY = 0;    // 一天展示5次
        int DEFAULT_SHOW_TIME_SPLIT = 60;    // 展示间隔
        int DEFAULT_FB_CLICK_MODEL = 2;    // 整形枚举 0-全局可点／1-局部可点／2-仅download可点（默认2）
        int DEFAULT_SWITCH = 1;  //广告开关
    }

    interface DownloadPlay {
        int DEFAULT_SHOW_TIMES_ADAY = 0;    // 一天展示0次
        int DEFAULT_SHOW_TIME_SPLIT = 0;    // 展示间隔
        int DEFAULT_SWITCH = 0;  //广告开关
    }

}
