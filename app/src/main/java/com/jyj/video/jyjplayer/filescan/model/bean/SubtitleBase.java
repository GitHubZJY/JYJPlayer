package com.jyj.video.jyjplayer.filescan.model.bean;


import com.jyj.video.jyjplayer.utils.VideoUtil;

/**
 * @author denglongyun
 * @date 17-12-8
 */

public class SubtitleBase extends AbstractBean {

    private String path;
    private String name;
    private String language; // 语言代码
    private long size; // 字幕文件大小
    private long createTime = 0L; // 创建时间

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    /**
     * 小数点后保留1位
     */
    public String getSizeAsStringWithUnit() {
        return VideoUtil.getSizeAsStringWithUnit(getSize());
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
