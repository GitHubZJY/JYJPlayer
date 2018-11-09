package com.jyj.video.jyjplayer.filescan.model.bean;

import android.graphics.Bitmap;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.utils.VideoUtil;

/**
 * @author denglongyun
 * @date 17-10-19
 */

public class VideoInfo extends AbstractBean {

    private String name;
    private String displayName; // 文件名
    private String path; // 绝对路径
    private String parentFileName; // 父文件夹名称-以此来归类
    private long size; // 大小
    private long duration = 0L; // 视频时长
    private String subtitlePath; // 字幕绝对路径
    private String subtitleName; // 字幕文件名
    private Bitmap icon;
    private boolean isPlaying = false; // 是否为正在播放的视频
    private long lastModify = 0L; // 创建或者最后修改时间
    private boolean isNew = false;
    private long createTime = 0L; // 创建时间

    private int tagResId = R.string.latest_early; // 本属性不存入DB

    private int matchPos = -1;
    private int percent = 100; // 视频完整度/下载比例

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParentFileName(String name) {
        this.parentFileName = name;
    }

    public String getParentFileName() {
        return parentFileName;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    /**
     * 小数点后保留2位
     */
    public String getSizeAsStringWithUnit() {
        return VideoUtil.getSizeAsStringWithUnit(getSize());
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setSubtitlePath(String subtitlePath) {
        this.subtitlePath = subtitlePath;
    }

    public String getSubtitlePath() {
        return subtitlePath;
    }

    public void setSubtitleName(String duration) {
        this.subtitleName = duration;
    }

    public String getSubtitleName() {
        return subtitleName;
    }

    public void setIcon(Bitmap iconBitmap) {
        this.icon = iconBitmap;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public long getLastModify() {
        return lastModify;
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

    // 返回最近新增分类名称ResId：
    public int getTag() {
        return tagResId;
    }

    public void setTag(int resId) {
        if (resId <= 0) {
            return;
        }
        tagResId = resId;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int per) {
        if (per < 0 || per > 100) {
            return;
        }
        percent = per;
    }

    // 返回最近新增分类名称ResId：
    public int getMatchPos() {
        return matchPos;
    }

    public void setMatchPos(int matchPos) {
        if (matchPos <= 0) {
            return;
        }
        this.matchPos = matchPos;
    }

    @Override
    public String toString() {
        return " name= " + name
                + " displayName= " + displayName
                + " parentFileName= " + parentFileName
                + " size= " + size
                + " duration= " + duration
                + " lastModify= " + lastModify
                + " createTime= " + createTime
                + " subtitleName= " + subtitleName
                + " isPlaying= " + isPlaying
                + " path= " + path
                + " subtitlePath= " + subtitlePath
                + " percent= " + percent
                + " isNew= " + isNew;
    }

}
