package com.jyj.video.jyjplayer.constant;

/**
 * @author zjyang
 * @date 17-10-18
 */

public interface SpConstant {

    //------------------------sp file name-----------------
    String DEFAULT_SP_FILE = "default_sharePreferences"; // 默认sp

    String SETTING = "profile_setting"; // 各设置项

    String ADS_FILE = "default_ads_file"; // 广告默认sp

    String AB_TEST_SP = "default_abtest_file"; // 广告默认sp


    //----------------------key----------------------
    String FIRST_START_TIME = "FIRST_START_TIME"; // 首次启动app时间

    String IS_AUTO_PLAY_NEXT = "IS_AUTO_PLAY_NEXT"; // 是否自动播放下一个视频
    String IS_HARDWARE_DECODING = "IS_HARDWARE_DECODING"; // 是否硬件解码
    String IS_SHOW_HIDDEN_FOLDER = "IS_SHOW_HIDDEN_FOLDER"; // 是否硬件解码
    String IS_CHECK_NO_MEDIA = "IS_CHECK_NO_MEDIA"; // 是否硬件解码
    String IS_FIRST_SCAN = "IS_FIRST_SCAN"; // 是否第一次进入app
    String IS_NEED_SCAN_BACKGROUND = "IS_NEED_SCAN_BACKGROUND"; // 是否第一次进入app

    String FIRST_OPEN_LAUNCHER_VC = "FIRST_OPEN_LAUNCHER_VC"; //第一次安装时的vc
    String HAVE_SHOW_NEW_VERSION_TIP = "HAVE_SHOW_NEW_VERSION_TIP";
    String IS_INSTALL_FROM_UPGRADE = "IS_INSTALL_FROM_UPGRADE";

    String TIME_19_UPLOAD = "TIME_19_UPLOAD";  //保存19协议统计上一次上传时间
    String ONLINE_SUBTITLE_TOKEN = "ONLINE_SUBTITLE_TOKEN";  //在线字幕请求token

    String AB_RESULT_KEY = "AB_RESULT_KEY";  //ABsdk数据
    String UPGRADE_DIALOG_SHOW_TIME = "UPGRADE_DIALOG_SHOW_TIME"; //更新弹框上次展示的时间
    String UPGRADE_DIALOG_SHOW_COUNT = "UPGRADE_DIALOG_SHOW_COUNT"; //更新弹框已展示次数


    //----------------------菜单----------------------
    String IS_OPEN_SUBTITLE = "IS_OPEN_SUBTITLE"; //是否开启了字幕
    String IS_FIRST_PLAY_VIDEO = "IS_FIRST_PLAY_VIDEO"; //是否是首次播放视频


    String ALL_VIDEO_DATE = "ALL_VIDEO_DATE"; //所有视频的日期  用于统计周几新增

    String FIRST_ENTER_INS_PAGE = "FIRST_ENTER_INS_PAGE"; //首次进入ins下载页


    //----------------------Ab配置信息缓存----------------------
    String IS_NEED_SHOW_UPGRADE = "IS_NEED_SHOW_UPGRADE"; //是否需要展示升级弹框
    String UPGRADE_DIALOG_MAX_SHOW_COUNT = "UPGRADE_DIALOG_MAX_SHOW_COUNT";  //更新弹框展示次数
    String UNLOCK_FULL_SCREEN_SHOW_TIME = "UNLOCK_FULL_SCREEN_SHOW_TIME"; //解锁伪全屏展示时间
    String UNLOCK_FULL_SCREEN_SPLIT_TIME = "UNLOCK_FULL_SCREEN_SPLIT_TIME"; //解锁伪全屏间隔时间
    String UNLOCK_FULL_SCREEN_START_TIME = "UNLOCK_FULL_SCREEN_START_TIME"; //解锁伪全屏开始时间
    String UNLOCK_FULL_SCREEN_END_TIME = "UNLOCK_FULL_SCREEN_END_TIME"; //解锁伪全屏结束时间
    String UNLOCK_FULL_SCREEN_CLICK_AREA = "UNLOCK_FULL_SCREEN_CLICK_AREA"; //解锁伪全屏点击区域
    String VIDEO_PAUSE_AD_SHOW_TIME = "VIDEO_PAUSE_AD_SHOW_TIME"; //视频暂停广告展示时间
    String VIDEO_PAUSE_AD_SPLIT_TIME = "VIDEO_PAUSE_AD_SPLIT_TIME"; //视频暂停广告间隔时间
    String VIDEO_PAUSE_AD_CLICK_AREA = "VIDEO_PAUSE_AD_CLICK_AREA"; //视频暂停广告点击区域

    String HOME_KEY_FULL_SCREEN_SHOW_TIME = "HOME_KEY_FULL_SCREEN_SHOW_TIME"; //Home键广告展示时间
    String HOME_KEY_FULL_SCREEN_SPLIT_TIME = "HOME_KEY_FULL_SCREEN_SPLIT_TIME"; //Home键广告间隔时间
    String HOME_KEY_FULL_SCREEN_START_TIME = "HOME_KEY_FULL_SCREEN_START_TIME"; //Home键广告开始时间
    String HOME_KEY_FULL_SCREEN_END_TIME = "HOME_KEY_FULL_SCREEN_END_TIME"; //Home键广告结束时间
    String HOME_KEY_FULL_SCREEN_CLICK_AREA = "HOME_KEY_FULL_SCREEN_CLICK_AREA"; //Home键广告点击区域
    String HOME_KEY_FULL_SCREEN_SWITCH = "HOME_KEY_FULL_SCREEN_SWITCH"; //Home键广告开关

    String DOWNLOAD_PLAY_FULL_SCREEN_SPLIT_TIME = "DOWNLOAD_PLAY_FULL_SCREEN_SPLIT_TIME"; //下载播放广告间隔时间
    String DOWNLOAD_PLAY_FULL_SCREEN_SHOW_TIME = "DOWNLOAD_PLAY_FULL_SCREEN_SHOW_TIME"; //下载播放广告展示时间
    String DOWNLOAD_PLAY_FULL_SCREEN_SWITCH = "DOWNLOAD_PLAY_FULL_SCREEN_SWITCH"; //下载播放广告开关

    String RATE_GUIDE_DIALOG_SWITCH = "RATE_GUIDE_DIALOG_SWITCH"; //评分引导弹框开关

    String IS_DOWNLOAD_YOUTUBE_FILM = "IS_DOWNLOAD_YOUTUBE_FILM"; //是否允许下载youtube视频链接


    //----------------------广告控制信息----------------------
    String SCREEN_ON_AD_ACTIVE = "screen_on_ad_active";
    String TIME_USER_PRESENT = "time_user_present";

    String SCREEN_ON_LAST_LOAD_SUCCESS_TIME = "last_load_screenon_ad_success_time";
    String SCREEN_ON_LAST_LOAD_TIME = "last_load_screenon_ad_time";
    String SCREEN_ON_SHOW_TIME = "show_screenon_ad_time";
    String SCREEN_ON_SHOW_DATE = "screenon_ad_show_times_today";
    String SCREEN_ON_SHOW_TIMES_TODAY = "screenon_ad_show_times_date";

    String PAUSE_LAST_LOAD_SUCCESS_TIME = "last_load_pause_ad_success_time";
    String PAUSE_LAST_LOAD_TIME = "last_load_pause_ad_time";
    String PAUSE_SHOW_TIME = "show_pause_ad_time";
    String PAUSE_SHOW_DATE = "pause_ad_show_times_date";
    String PAUSE_SHOW_TIMES_TODAY = "pause_ad_show_times_today";

    String HOME_KEY_LAST_LOAD_SUCCESS_TIME = "last_load_home_key_ad_success_time";
    String HOME_KEY_LAST_LOAD_TIME = "last_load_home_key_ad_time";
    String HOME_KEY_SHOW_TIME = "show_home_key_ad_time";
    String HOME_KEY_SHOW_DATE = "home_key_ad_show_times_date";
    String HOME_KEY_SHOW_TIMES_TODAY = "home_key_ad_show_times_today";

    String SUSPENSION_LAST_LOAD_SUCCESS_TIME = "last_load_suspension_ad_success_time";
    String SUSPENSION_LAST_LOAD_TIME = "last_load_suspension_ad_time";
    String SUSPENSION_SHOW_TIME = "show_suspension_ad_time";
    String SUSPENSION_SHOW_DATE = "suspension_ad_show_times_date";
    String SUSPENSION_SHOW_TIMES_TODAY = "suspension_ad_show_times_today";

    String DOWNLOAD_PLAY_LAST_LOAD_SUCCESS_TIME = "last_load_download_play_ad_success_time";
    String DOWNLOAD_PLAY_LAST_LOAD_TIME = "last_load_download_play_ad_time";
    String DOWNLOAD_PLAY_SHOW_TIME = "show_download_play_ad_time";
    String DOWNLOAD_PLAY_SHOW_DATE = "download_play_ad_show_times_date";
    String DOWNLOAD_PLAY_SHOW_TIMES_TODAY = "download_play_ad_show_times_today";


    String ADVERTISE_LAST_SHOW_TIME = "advertise_last_show_time";

    String CLICK_UNLOCK_SCREEN_AD_TIME= "click_unlock_screen_ad_time"; // 点击解锁伪全屏广告时间-adw打点用

    String LAST_CLICK_HOME_KEY_TIME = "LAST_CLICK_HOME_KEY_TIME"; //上次点击home键的时间


    //----------------------隐私空间----------------------
    String PRIVACY_SPACE_PASSWORD = "PRIVACY_SPACE_PASSWORD";  //隐私空间密码
    String HAS_CLICK_TRY_PRIVACY = "HAS_CLICK_TRY_PRIVACY";  //是否点击过try按钮

    /**
     * 本地ABTest的类型 具体方案
     */
    String AB_SCHEME_LOCAL = "scheme_local";

    //----------------------评分引导----------------------
    String PLAY_VIDEO_COUNT = "PLAY_VIDEO_COUNT";   //播放视频的次数(3次就弹框)
    String RATE_DIALOG_HAVE_SHOW = "RATE_DIALOG_HAVE_SHOW";  //是否展示过评分引导弹框

    String IS_UPLOAD_INSTALL_INS_STATISTIC = "IS_UPLOAD_INSTALL_INS_STATISTIC";  //记录是否上传过是否安装ins的统计


    String CONFIG_FILM_PREVIEW_LIST = "CONFIG_FILM_PREVIEW_LIST"; //配置后台在线电影推荐信息列表
    String CONFIG_RECOMMEND_WEB_LIST = "CONFIG_RECOMMEND_WEB_LIST"; //推荐网址列表

    String HAS_DOWNLOADING_IJKLIB = "HAS_DOWNLOAD_IJKLIB"; //是否已下载过ijk的解码器
    String HAS_UNSUPPORT_VIDEO = "HAS_UNSUPPORT_VIDEO"; //当前设备有系统播放器不支持的视频格式
    String DOWNLOAD_IJKLIB_PAUSE_PERCENT = "DOWNLOAD_IJKLIB_PAUSE_PERCENT";  //保存下载暂停时的当前进度

    String REQUEST_FILM_DATA_LIST = "REQUEST_FILM_DATA_LIST"; //服务器下发的电影数据（非后台配置下发）
    String CACHE_FILM_DATA_TYPE = "CACHE_FILM_DATA_TYPE"; //服务器下发的电影数据 所属分类
    String REQUEST_THEME_FILM_DATA_LIST = "REQUEST_THEME_FILM_DATA_LIST"; //服务器下发的主题分类电影数据（非后台配置下发）
    String FILM_HOME_PAGE_FIRST_LOAD_TIME = "FILM_HOME_PAGE_FIRST_LOAD_TIME"; //在线电影首页首次开始请求的时间

    String CONFIG_SEARCH_TAB_LIST = "CONFIG_SEARCH_TAB_LIST"; //搜索tab的ab下发数据
    String CHECK_VIDEO_URL_FIRST = "CHECK_VIDEO_URL_FIRST"; //首次检测到可下载播放的视频网址
    String DOWNLOAD_BTN_CLICKFIRST = "DOWNLOAD_BTN_CLICK_FIRST"; //首次点击下载检测视频按钮

    String DOWNLOAD_AUTO_PAUSE_LIST = "DOWNLOAD_AUTO_PAUSE_LIST"; //自动暂停的电影下载列表
    String SUBTITLE_LANGUAGE_LIST = "SUBTITLE_LANGUAGE_LIST"; //字幕语言列表

    String PRIVACY_GUIDE_FIRST_SHOW = "PRIVACY_GUIDE_FIRST_SHOW"; //首次展示隐私空间 隐私网站的引导
    String PRIVACY_WEBSITE_LIST = "PRIVACY_WEBSITE_LIST"; //隐私空间的隐私网站的ab下发数据
    String PRIVACY_WEBSITE_PANL_STATUS = "PRIVACY_WEBSITE_PANL_STATUS"; //隐私空间的隐私网站面板的展开或折叠状态
    String SEARCH_HISTORY = "SEARCH_HISTORY"; //搜索历史记录
    String SEARCH_TIPS_CONTAINER = "SEARCH_TIPS_CONTAINER"; //搜索提示弹框展示次数
}
