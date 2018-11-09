package com.jyj.video.jyjplayer.db.bean;



import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by zhengjiayang on 2018/5/8.
 */
@Entity
public class DownFilm {

    @Id
    @NotNull
    @Unique
    private String url; // 下载文件url
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
    private boolean isVideo = false;
    private String lastFinishPath;
    private int downEntrance;
    private String webViewUrl;

    @Generated(hash = 279138035)
    public DownFilm(@NotNull String url, String path, String fileType, String displayName,
            long totalLength, long lastModify, long createTime, String fileName, long currentLength,
            float percentage, long date, int status, boolean isVideo, String lastFinishPath,
            int downEntrance, String webViewUrl) {
        this.url = url;
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
        this.isVideo = isVideo;
        this.lastFinishPath = lastFinishPath;
        this.downEntrance = downEntrance;
        this.webViewUrl = webViewUrl;
    }
    @Generated(hash = 885304703)
    public DownFilm() {
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
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
    public int getDownEntrance() {
        return this.downEntrance;
    }
    public void setDownEntrance(int downEntrance) {
        this.downEntrance = downEntrance;
    }

    public DownFilm(@NotNull DownLoadFilmInfo bean) {
        this.url = bean.getUrl();
        this.fileName = bean.getFileName();
        this.fileType = bean.getFileType();
        this.displayName = bean.getDisplayName();
        this.path = bean.getPath();
        this.currentLength = bean.getCurrentLength();
        this.totalLength = bean.getTotalLength();
        this.percentage = bean.getPercentage();
        this.lastModify = bean.getLastModify();
        this.createTime = System.currentTimeMillis();
        this.date = System.currentTimeMillis();
        this.status = bean.getStatus();
        this.isVideo = bean.isVideo();
        this.lastFinishPath = bean.getLastFinishPath();
        this.downEntrance = bean.getDownEntrance();
        this.webViewUrl = bean.getmWebViewUrl();
    }

    public static DownLoadFilmInfo switchDownloadInfo(@NotNull DownFilm download) {
        if (download == null) {
            return null;
        }

        DownLoadFilmInfo downloadInfo = new DownLoadFilmInfo();
        downloadInfo.setUrl(download.getUrl());
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
        downloadInfo.setVideo(download.getIsVideo());
        downloadInfo.setLastFinishPath(download.getLastFinishPath());
        downloadInfo.setDownEntrance(download.getDownEntrance());
        downloadInfo.setmWebViewUrl(download.getWebViewUrl());
        return downloadInfo;
    }
    public String getWebViewUrl() {
        return this.webViewUrl;
    }
    public void setWebViewUrl(String mWebViewUrl) {
        this.webViewUrl = mWebViewUrl;
    }

}
