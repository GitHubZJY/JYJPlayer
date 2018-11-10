package com.jyj.video.jyjplayer.db.bean;

import android.graphics.Bitmap;

import com.jyj.video.jyjplayer.AppApplication;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @author zjyang
 * @date 17-11-22
 */

@Entity
public class Thumbnail {

    @Id
    @NotNull
    @Unique
    private String path;
    private byte[] thumbnail;



    @Generated(hash = 1212519895)
    public Thumbnail(@NotNull String path, byte[] thumbnail) {
        this.path = path;
        this.thumbnail = thumbnail;
    }



    @Generated(hash = 248300619)
    public Thumbnail() {
    }



    public static Bitmap getThumbnailFromJson(String videoJson) {
        return AppApplication.getGson().fromJson(videoJson, Bitmap.class);
    }



    public String getPath() {
        return this.path;
    }



    public void setPath(String path) {
        this.path = path;
    }



    public byte[] getThumbnail() {
        return this.thumbnail;
    }



    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
}
