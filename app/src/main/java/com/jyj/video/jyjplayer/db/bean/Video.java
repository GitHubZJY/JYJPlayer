package com.jyj.video.jyjplayer.db.bean;

import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @author denglongyun
 * @date 17-11-7
 */
@Entity
public class Video {


    @Id
    @NotNull
    @Unique
    private String path; // 绝对路径
    private String name;
    private String displayName; // 文件名
    private String parentFileName; // 父文件夹名称-以此来归类
    private long size; // 大小
    private long duration; // 视频时长
    private String subtitlePath; // 字幕绝对路径
    private String subtitleName; // 字幕文件名
    private long lastModify = 0L; // 创建或者最后修改时间
    private boolean isNew = false;
    private long createTime = 0L; // 创建时间

    public long getLastModify() {
        return this.lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public String getSubtitleName() {
        return this.subtitleName;
    }

    public void setSubtitleName(String subtitleName) {
        this.subtitleName = subtitleName;
    }

    public String getSubtitlePath() {
        return this.subtitlePath;
    }

    public void setSubtitlePath(String subtitlePath) {
        this.subtitlePath = subtitlePath;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getParentFileName() {
        return this.parentFileName;
    }

    public void setParentFileName(String parentFileName) {
        this.parentFileName = parentFileName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Generated(hash = 2046109775)
    public Video(@NotNull String path, String name, String displayName, String parentFileName,
                 long size, long duration, String subtitlePath, String subtitleName, long lastModify,
                 boolean isNew, long createTime) {
        this.path = path;
        this.name = name;
        this.displayName = displayName;
        this.parentFileName = parentFileName;
        this.size = size;
        this.duration = duration;
        this.subtitlePath = subtitlePath;
        this.subtitleName = subtitleName;
        this.lastModify = lastModify;
        this.isNew = isNew;
        this.createTime = createTime;
    }

    @Generated(hash = 237528154)
    public Video() {
    }

    //----------------------------------setter getter 分隔线----------------------------------------//


    public Video(@NotNull VideoInfo videoInfo) {
        this.path = videoInfo.getPath();
        this.name = videoInfo.getName();
        this.displayName = videoInfo.getDisplayName();
        this.parentFileName = videoInfo.getParentFileName();
        this.size = videoInfo.getSize();
        this.duration = videoInfo.getDuration();
        this.subtitlePath = videoInfo.getSubtitlePath();
        this.subtitleName = videoInfo.getSubtitleName();
        this.lastModify = videoInfo.getLastModify();
        this.isNew = videoInfo.getIsNew();
        this.createTime = videoInfo.getCreateTime();
    }

    public static VideoInfo switchVideoInfo(@NotNull Video video) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setPath(video.getPath());
        videoInfo.setName(video.getName());
        videoInfo.setDisplayName(video.getDisplayName());
        videoInfo.setParentFileName(video.getParentFileName());
        videoInfo.setSize(video.getSize());
        videoInfo.setDuration(video.getDuration());
        videoInfo.setSubtitlePath(video.getSubtitlePath());
        videoInfo.setSubtitleName(video.getSubtitleName());
        videoInfo.setIsNew(video.getIsNew());
        videoInfo.setLastModify(video.getLastModify());
        videoInfo.setCreateTime(video.getCreateTime());
        return videoInfo;

    }

}
