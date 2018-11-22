package com.jyj.video.jyjplayer.subtitle.bean;

/**
 * Created by zhengjiayang on 2017/10/25.
 */

public class SRT {

    //字幕开始时间
    private int beginTime;
    //字幕结束时间
    private int endTime;
    //字幕内容
    private String srtBody;

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getSrtBody() {
        return srtBody;
    }

    public void setSrtBody(String srtBody) {
        this.srtBody = srtBody;
    }

    @Override
    public String toString() {
        return "" + beginTime + ":" + endTime + ":" + srtBody;
    }
}
