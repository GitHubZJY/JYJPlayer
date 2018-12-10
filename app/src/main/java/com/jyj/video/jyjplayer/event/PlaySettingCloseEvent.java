package com.jyj.video.jyjplayer.event;

/**
 * Created by zhengjiayang on 2017/10/27.
 */

public class PlaySettingCloseEvent {

    public static final int SUBTITLE_PANEL = 1;
    public static final int SPEED_PANEL = 2;
    private int mPanelType;
    private String mSrtFileUrl;

    public PlaySettingCloseEvent(){

    }

    public PlaySettingCloseEvent(String srtFileUrl) {
        this.mSrtFileUrl = srtFileUrl;
    }

    public String getSrtFileUrl() {
        return mSrtFileUrl;
    }

    public void setSrtFileUrl(String mSrtFileUrl) {
        this.mSrtFileUrl = mSrtFileUrl;
    }

    public int getPanelType() {
        return mPanelType;
    }

    public void setPanelType(int mPanelType) {
        this.mPanelType = mPanelType;
    }
}
