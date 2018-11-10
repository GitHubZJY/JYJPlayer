package com.jyj.video.jyjplayer.download.bean;


import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.filescan.model.bean.AbstractBean;
import com.jyj.video.jyjplayer.utils.SdcardUtil;
import com.jyj.video.jyjplayer.utils.VideoUtil;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * 下载成功的视频/图片
 *
 * @author zjyang
 * @date 17-11-7
 */
public class DownloadSidecar extends AbstractBean {


    private String url; // 下载文件url
    private String originalUrl; // 对应的insUrl 等
    private String path; // 储存路径
    private String fileType; // 文件名类型后缀
    private long totalLength = 0L;
    private long lastModify = 0L;
    private long createTime = 0L;

    private String fileName; // 储存文件名
    private long currentLength = 0L;
    private float percentage = 0f;
    private long date = 0L;
    private int status = Constant.Status.NONE;
    private int type = InfoType.VIDEO; // 图片？ 视频？ 组合?
    private boolean isVideo = false;
    private boolean isDownloading = false;


    public DownloadSidecar(@NotNull String url, String originalUrl, String path, String fileType,
                           long totalLength, long lastModify, long createTime,
                           String fileName, long currentLength, float percentage, long date, int status,
                           int type, boolean isVideo, boolean isDownloading) {
        this.url = url;
        this.originalUrl = originalUrl;
        this.path = path;
        this.fileType = fileType;
        this.totalLength = totalLength;
        this.lastModify = lastModify;
        this.createTime = createTime;
        this.fileName = fileName;
        this.currentLength = currentLength;
        this.percentage = percentage;
        this.date = date;
        this.status = status;
        this.type = type;
        this.isVideo = isVideo;
        this.isDownloading = isDownloading;
    }

    public DownloadSidecar() {
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

    public boolean getIsVideo() {
        return this.isVideo;
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public boolean getIsDownloading() {
        return this.isDownloading;
    }

    public void setIsDownloading(boolean downloading) {
        this.isDownloading = downloading;
    }


    public DownloadSidecar(@NotNull String url, @NotNull String originalUrl, int type) {
        this.url = url;
        this.originalUrl = originalUrl;
        this.fileName = VideoUtil.getVideoName(url);
        this.fileType = VideoUtil.getFileSuffix(url);
        this.path = SdcardUtil.DOWNLOAD_SAVE_PATH + this.fileName;
        this.currentLength = 0L;
        this.totalLength = 0L;
        this.percentage = 0f;
        this.lastModify = 0L;
        this.createTime = System.currentTimeMillis();
        this.date = System.currentTimeMillis();
        this.status = Constant.Status.NONE;
        this.type = type;
        this.isVideo = type == InfoType.VIDEO;
        this.isDownloading = false;
    }

    public String toString() {
        return " url=" + url + " originalUrl=" + originalUrl + " fileName=" + fileName + " fileType=" + fileType + " isDownloading=" + isDownloading
                + " type=" + type + " path=" + path + " currentLength=" + currentLength + " totalLength=" + totalLength
                + " lastModify=" + lastModify + " createTime=" + createTime + " date=" + date + " status=" + status
                + " percentage=" + percentage;
    }
}
