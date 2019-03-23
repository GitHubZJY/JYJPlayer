package com.jyj.video.jyjplayer.module.download.event;

/**
 * Created by 74215 on 2019/1/20.
 */

public class StartDownLoadEvent {

    private String url;

    public StartDownLoadEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
