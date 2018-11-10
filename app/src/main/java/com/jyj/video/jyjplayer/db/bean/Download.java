package com.jyj.video.jyjplayer.db.bean;


import com.google.gson.reflect.TypeToken;
import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.download.bean.DownloadInfo;
import com.jyj.video.jyjplayer.download.bean.DownloadSidecar;
import com.jyj.video.jyjplayer.filescan.model.bean.AbstractBean;
import com.jyj.video.jyjplayer.utils.SdcardUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * 下载成功的视频/图片
 *
 * @author zjyang
 * @date 17-11-7
 */
@Entity
public class Download extends AbstractBean {


    @Id
    @NotNull
    @Unique
    private String url; // 下载文件url
    private String originalUrl; // 对应的insUrl 等
    private String path; // 储存路径
    private String fileType; // 文件名类型后缀
    private String displayName;
    private long totalLength = 0L;
    private long lastModify = 0L;
    private long createTime = 0L;

    private String fileName; // 储存文件名
    private long currentLength = 0L;
    private float percentage = 0f;
    private long date = 0L;
    private int status = Constant.Status.NONE;
    private int type = InfoType.VIDEO; // 图片？ 视频？ 组合?

    private String sidecars;
    private boolean isVideo = false;
    private String lastFinishPath;
    private String completeness = "0/1";

    @Generated(hash = 42771728)
    public Download(@NotNull String url, String originalUrl, String path, String fileType, String displayName,
            long totalLength, long lastModify, long createTime, String fileName, long currentLength, float percentage,
            long date, int status, int type, String sidecars, boolean isVideo, String lastFinishPath, String completeness) {
        this.url = url;
        this.originalUrl = originalUrl;
        this.path = path;
        this.fileType = fileType;
        this.displayName = displayName;
        this.totalLength = totalLength;
        this.lastModify = lastModify;
        this.createTime = createTime;
        this.fileName = fileName;
        this.currentLength = currentLength;
        this.percentage = percentage;
        this.date = date;
        this.status = status;
        this.type = type;
        this.sidecars = sidecars;
        this.isVideo = isVideo;
        this.lastFinishPath = lastFinishPath;
        this.completeness = completeness;
    }

    @Generated(hash = 1462805409)
    public Download() {
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalUrl() {
        return this.originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getTotalLength() {
        return this.totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getLastModify() {
        return this.lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCurrentLength() {
        return this.currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public float getPercentage() {
        return this.percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSidecars() {
        return this.sidecars;
    }

    public void setSidecars(String sidecars) {
        this.sidecars = sidecars;
    }

    public boolean getIsVideo() {
        return this.isVideo;
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public String getLastFinishPath() {
        return this.lastFinishPath;
    }

    public void setLastFinishPath(String lastFinishPath) {
        this.lastFinishPath = lastFinishPath;
    }

    public String getCompleteness() {
        return this.completeness;
    }

    public void setCompleteness(String completeness) {
        this.completeness = completeness == null ? "0/1" : completeness;
    }


    public Download(@NotNull DownloadInfo bean) {
        this.url = bean.getUrl();
        this.fileName = bean.getFileName();
        this.fileType = bean.getFileType();
        this.displayName = bean.getDisplayName();
        this.originalUrl = bean.getOriginalUrl();
        this.path = SdcardUtil.DOWNLOAD_SAVE_PATH + this.fileName;
        this.currentLength = bean.getCurrentLength();
        this.totalLength = bean.getTotalLength();
        this.percentage = bean.getPercentage();
        this.lastModify = bean.getLastModify();
        this.createTime = System.currentTimeMillis();
        this.date = System.currentTimeMillis();
        this.status = bean.getStatus();
        this.type = bean.getType();
        this.sidecars = AppApplication.getGson().toJson(bean.getDownloadList());
        this.isVideo = bean.getIsVideo();
        this.lastFinishPath = bean.getLastFinishPath();
        this.completeness = bean.getCompleteness();
    }

    public static DownloadInfo switchDownloadInfo(@NotNull Download download) {
        if (download == null) {
            return null;
        }

        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setUrl(download.getUrl());
        downloadInfo.setOriginalUrl(download.getOriginalUrl());
        downloadInfo.setFileName(download.getFileName());
        downloadInfo.setFileType(download.getFileType());
        downloadInfo.setDisplayName(download.getDisplayName());
        downloadInfo.setPath(download.getPath());
        downloadInfo.setCurrentLength(download.getCurrentLength());
        downloadInfo.setTotalLength(download.getTotalLength());
        downloadInfo.setPercentage(download.getPercentage());
        downloadInfo.setLastModify(download.getLastModify());
        downloadInfo.setCreateTime(download.getCreateTime());
        downloadInfo.setDate(download.getDate());
        downloadInfo.setStatus(download.getStatus());
        downloadInfo.setDownloadList(getDownloadListFromJson(download.getSidecars()));
        downloadInfo.setIsVideo(download.getIsVideo());
        downloadInfo.setType(download.getType());
        downloadInfo.setLastFinishPath(download.getLastFinishPath());
        downloadInfo.setCompleteness(download.getCompleteness());

        return downloadInfo;
    }

    public static List<DownloadSidecar> getDownloadListFromJson(String sidecarJson) {
        return AppApplication.getGson().fromJson(sidecarJson, new TypeToken<List<DownloadSidecar>>() {
        }.getType());
    }

    public String toString() {
        return " currentLength=" + currentLength + " totalLength=" + totalLength + " percentage=" + percentage
                + " lastModify=" + lastModify + " createTime=" + createTime + " date=" + date + " status=" + status
                + " url=" + url + " originalUrl=" + originalUrl + " fileName=" + fileName + " fileType=" + fileType
                + " type=" + type + " path=" + path + " displayName=" + displayName;
    }
}
