package com.jyj.video.jyjplayer.event;

/**
 * Created by zhengjiayang on 2017/10/27.
 */

public class PlaySettingCloseEvent {

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
}
