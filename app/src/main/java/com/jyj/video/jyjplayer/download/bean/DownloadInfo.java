package com.jyj.video.jyjplayer.download.bean;

import android.text.TextUtils;

import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.filescan.model.bean.AbstractBean;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.utils.SdcardUtil;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载成功的视频/图片
 *
 * @author zjyang
 * @date 17-11-7
 */
public class DownloadInfo extends AbstractBean {


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
    private boolean isVideo = false;
    private String lastFinishPath;
    private String completeness = "0/1";

    private List<DownloadSidecar> downloadList = new ArrayList<DownloadSidecar>();


    public DownloadInfo(@NotNull String url, String originalUrl, String path, String fileType,
                        String displayName, long totalLength, long lastModify, long createTime,
                        String fileName, long currentLength, float percentage, long date, int status,
                        int type, boolean isVideo, String lastFinishPath, String completeness, List<DownloadSidecar> downloadList) {
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
        this.isVideo = isVideo;
        this.downloadList = downloadList;
        this.lastFinishPath = lastFinishPath;
        this.completeness = completeness;
    }

    public DownloadInfo() {
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
        this.completeness = completeness;
    }


    public void setDownloadList(List<DownloadSidecar> list) {
        if (list != null && !list.isEmpty()) {
            if (downloadList != null) {
                this.downloadList.clear();
            } else {
                this.downloadList = new ArrayList<DownloadSidecar>();
            }
            this.downloadList.addAll(list);
        }
    }

    public List<DownloadSidecar> getDownloadList() {
        return this.downloadList;
    }

    /**
     * @return list为空时返回-1
     *
     */
    public int getDownloadCnt() {
        return downloadList == null ? 0 : downloadList.size();
    }

    public void addAllDownloadInfo(List<DownloadSidecar> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i ++) {
                addDownloadInfo(list.get(i));
            }
        }
    }

    public void addDownloadInfo(DownloadSidecar sidecar) {
        if (sidecar != null) {
            boolean isExist = false;
            for (DownloadSidecar info : downloadList) {
                if (info.getPath().equals(sidecar.getPath())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                downloadList.add(sidecar);
            }
        }
    }

    public DownloadSidecar getDownloadSidecar(String sidecarUrl) {
        if (TextUtils.isEmpty(sidecarUrl)) {
            return null;
        }
        for (DownloadSidecar info : downloadList) {
            if (info.getUrl().equals(sidecarUrl)) {
                return info;
            }
        }
        return null;
    }


    public static List<VideoInfo> getVideoInfo(@NotNull DownloadInfo download) {
        List<VideoInfo> resultList = new ArrayList<VideoInfo>();

        if (download.getType() == InfoType.SIDECAR) {
            List<DownloadSidecar> sidecars = download.getDownloadList();
            if (sidecars == null || sidecars.isEmpty()) {
                return resultList;
            }
            for (DownloadSidecar s : sidecars) {
                if (download.getType() != InfoType.VIDEO) {
                    continue;
                }

                VideoInfo v = new VideoInfo();
                v.setPath(s.getPath());
                v.setParentFileName("download");
                v.setLastModify(s.getLastModify());
                v.setSize(s.getTotalLength());
                v.setName(s.getFileName());
                v.setDisplayName(download.getDisplayName());
                v.setCreateTime(s.getCreateTime());
                v.setPercent((int) s.getPercentage());
                resultList.add(v);
            }
        } else if (download.getType() == InfoType.VIDEO) {
            VideoInfo v = new VideoInfo();
            v.setPath(download.getPath());
            v.setParentFileName("download");
            v.setLastModify(download.getLastModify());
            v.setSize(download.getTotalLength());
            v.setName(download.getFileName());
            v.setDisplayName(download.getDisplayName());
            v.setCreateTime(download.getCreateTime());
            v.setPercent((int) download.getPercentage());
            resultList.add(v);
        }
        return resultList;
    }

    public String toString() {
        return " currentLength=" + currentLength + " totalLength=" + totalLength + " percentage=" + percentage
                + " lastModify=" + lastModify + " createTime=" + createTime + " date=" + date + " status=" + status
                + " url=" + url + " originalUrl=" + originalUrl + " fileName=" + fileName + " fileType=" + fileType
                + " type=" + type + " path=" + path + " displayName=" + displayName;
    }
}
