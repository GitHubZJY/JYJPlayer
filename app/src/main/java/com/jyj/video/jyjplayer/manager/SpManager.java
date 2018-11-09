package com.jyj.video.jyjplayer.manager;


import com.jyj.video.jyjplayer.constant.SpConstant;
import com.zjyang.base.utils.SpUtils;

/**
 * @author denglongyun
 * @date 17-11-3
 */

public class SpManager {

    private static SpManager sSettingConfig;
    private final static Object LOCK = new Object();

    private SpManager() {

    }

    public static SpManager getInstance() {
        if (sSettingConfig == null) {
            synchronized (LOCK) {
                if (sSettingConfig == null) {
                    sSettingConfig = new SpManager();
                }
            }
        }
        return sSettingConfig;
    }

    private SpUtils getSpUtils() {
        return SpUtils.obtain(SpConstant.SETTING);
    }

    public void setAutoPlayNext(boolean isAutoNext) {
        getSpUtils().save(SpConstant.IS_AUTO_PLAY_NEXT, isAutoNext);
    }

    // 默认: true
    public boolean getAutoPlayNext() {
        return getSpUtils().getBoolean(SpConstant.IS_AUTO_PLAY_NEXT, true);
    }

    public void setIsHardwareDecoding(boolean isHardwareDecoding) {
        getSpUtils().save(SpConstant.IS_HARDWARE_DECODING, isHardwareDecoding);
    }

    // 默认: true
    public boolean getIsHardwareDecoding() {
        return getSpUtils().getBoolean(SpConstant.IS_HARDWARE_DECODING, true);
    }

    public void setIsShowHiddenFolder(boolean isShowHiddenFolder) {
        getSpUtils().save(SpConstant.IS_SHOW_HIDDEN_FOLDER, isShowHiddenFolder);
    }

    // 默认: false
    public boolean getIsShowHiddenFolder() {
        return getSpUtils().getBoolean(SpConstant.IS_SHOW_HIDDEN_FOLDER, false);
    }

    public void setIsCheckNoMediaFile(boolean isCheckNoMediaFile) {
        getSpUtils().save(SpConstant.IS_CHECK_NO_MEDIA, isCheckNoMediaFile);
    }

    // 默认: false
    public boolean getIsCheckNoMediaFile() {
        return getSpUtils().getBoolean(SpConstant.IS_CHECK_NO_MEDIA, false);
    }

    public void setIsFirstScan(boolean isCheckNoMediaFile) {
        getSpUtils().save(SpConstant.IS_FIRST_SCAN, isCheckNoMediaFile);
    }

    // 默认: true
    public boolean getIsFirstScan() {
        return getSpUtils().getBoolean(SpConstant.IS_FIRST_SCAN, true);
    }

    // 后台被杀后 重新进入 自动扫描一次
    public void setIsNeedScanBackground(boolean isCheckNoMediaFile) {
        getSpUtils().save(SpConstant.IS_NEED_SCAN_BACKGROUND, isCheckNoMediaFile);
    }

    // 默认: false
    public boolean getIsNeedScanBackground() {
        return getSpUtils().getBoolean(SpConstant.IS_NEED_SCAN_BACKGROUND, false);
    }

    // 所有视频的日期  用于统计周几新增
    public void setAllVideoDate(String allDateStr) {
        getSpUtils().save(SpConstant.ALL_VIDEO_DATE, allDateStr);
    }

    // 默认: null
    public String getAllVideoDate() {
        return getSpUtils().getString(SpConstant.ALL_VIDEO_DATE, null);
    }



}
