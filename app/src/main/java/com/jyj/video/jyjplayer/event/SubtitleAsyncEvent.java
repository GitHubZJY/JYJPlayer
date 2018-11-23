package com.jyj.video.jyjplayer.event;

/**
 * Created by zhengjiayang on 2017/11/15.
 * 字幕解析结果
 */

public class SubtitleAsyncEvent {

    private boolean isSuccess;
    private boolean isAuto;

    public SubtitleAsyncEvent(boolean isSuccess, boolean isAuto) {
        this.isSuccess = isSuccess;
        this.isAuto = isAuto;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }
}
