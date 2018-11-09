package com.jyj.video.jyjplayer.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @author denglongyun
 * @date 17-11-22
 */

@Entity
public class Duration {

    @Id
    @NotNull
    @Unique
    private String path;
    private long duration;

    @Generated(hash = 576030946)
    public Duration(@NotNull String path, long duration) {
        this.path = path;
        this.duration = duration;
    }

    @Generated(hash = 720654702)
    public Duration() {
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
