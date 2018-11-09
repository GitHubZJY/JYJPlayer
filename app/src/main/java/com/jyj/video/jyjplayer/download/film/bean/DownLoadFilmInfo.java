package com.jyj.video.jyjplayer.download.film.bean;

import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.filescan.model.bean.AbstractBean;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;

import org.greenrobot.greendao.annotation.NotNull;

import static com.jyj.video.jyjplayer.utils.SdcardUtil.DOWNLOAD_FILM_SAVE_FOLDER_NAME;

/**
 * Created by zhengjiayang on 2018/5/7.
 */

public class DownLoadFilmInfo extends AbstractBean {

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
    private long downLoadSpeed = 0L;
    private int remainSecond = 0; //剩余秒数
    private boolean isPlaying;
    private int downEntrance; //记录下载来源;
    private String mWebViewUrl;

    public String getmWebViewUrl() {
        return mWebViewUrl;
    }

    public void setmWebViewUrl(String mWebViewUrl) {
        this.mWebViewUrl = mWebViewUrl;
    }

    public int getDownEntrance() {
        return downEntrance;
    }

    public void setDownEntrance(int downEntrance) {
        this.downEntrance = downEntrance;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getRemainSecond() {
        return remainSecond;
    }

    public void setRemainSecond(int remainSecond) {
        this.remainSecond = remainSecond;
    }

    public long getDownLoadSpeed() {
        return downLoadSpeed;
    }

    public void setDownLoadSpeed(long downLoadSpeed) {
        this.downLoadSpeed = downLoadSpeed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getLastFinishPath() {
        return lastFinishPath;
    }

    public void setLastFinishPath(String lastFinishPath) {
        this.lastFinishPath = lastFinishPath;
    }

    public static VideoInfo getVideoInfo(@NotNull DownLoadFilmInfo download) {
        VideoInfo v = new VideoInfo();
        v.setPath(download.getPath());
        v.setParentFileName(DOWNLOAD_FILM_SAVE_FOLDER_NAME);
        v.setLastModify(download.getLastModify());
        v.setSize(download.getTotalLength());
        v.setName(download.getFileName());
        v.setDisplayName(download.getDisplayName());
        v.setCreateTime(download.getCreateTime());
        v.setPercent((int) download.getPercentage());
        return v;
    }

    public String toString() {
        return " currentLength=" + currentLength + " totalLength=" + totalLength + " percentage=" + percentage
                + " lastModify=" + lastModify + " createTime=" + createTime + " date=" + date + " status=" + status
                + " url=" + url  + " fileName=" + fileName + " fileType=" + fileType
                + " path=" + path + " displayName=" + displayName;
    }
}
