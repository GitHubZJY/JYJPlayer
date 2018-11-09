package com.jyj.video.jyjplayer.filescan.model.bean;

import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.utils.VideoUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author denglongyun
 * @date 17-10-19
 */

public class FolderInfo extends AbstractBean {


    private String name;
    private String displayName;
    private String path;
    private long videosSizeSum;
    private int type = InfoType.DIRECTORY;
    private boolean isNew = false;
    private long lastModify = 0L; // 最后修改时间
    private long createTime = 0L; // 创建时间

    private List<VideoInfo> videoList = new ArrayList<VideoInfo>();

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

    public void setSize(long size) {
        this.videosSizeSum = size;
    }

    public long getSize() {
        return videosSizeSum;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * 小数点后保留2位
     */
    public String getSizeAsStringWithUnit() {
        return VideoUtil.getSizeAsStringWithUnit(getSize());
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

    public void setVideoList(List<VideoInfo> list) {
        if (list != null && !list.isEmpty()) {
            if (videoList != null) {
                this.videoList.clear();
            } else {
                this.videoList = new ArrayList<VideoInfo>();
            }
            this.videoList.addAll(list);
        }
    }

    public List<VideoInfo> getVideoList() {
        return this.videoList;
    }

    /**
     * @return list为空时返回-1
     *
     */
    public int getVideoCnt() {
        return videoList == null ? 0 : videoList.size();
    }

    public void addAllVideoInfo(List<VideoInfo> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i ++) {
                addVideoInfo(list.get(i));
            }
        }
    }

    public void addVideoInfo(VideoInfo videoInfo) {
        if (videoInfo != null) {
            boolean isExist = false;
            for (VideoInfo info : videoList) {
                if (info.getPath().equals(videoInfo.getPath())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                videoList.add(videoInfo);
            }
        }
    }

    public void removeVedioInfo(VideoInfo videoInfo) {
        if (videoInfo != null) {
            Iterator<VideoInfo> iterator = videoList.iterator();
            while (iterator.hasNext()) {
                VideoInfo info = iterator.next();
                if (info.getPath().equals(videoInfo.getPath())) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 结果可能为空
     * @return
     */
    public VideoInfo getPre() {
        if (videoList == null || videoList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < videoList.size(); i ++) {
            VideoInfo videoInfo = videoList.get(i);
            if (videoInfo != null && videoInfo.getIsPlaying()) {
                return i - 1 < 0 ? null : videoList.get(i - 1);
            }
        }
        return null;

    }

    /**
     * 结果可能为空
     * @return
     */
    public VideoInfo getNext() {
        if (videoList == null || videoList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < videoList.size(); i ++) {
            VideoInfo videoInfo = videoList.get(i);
            if (videoInfo != null && videoInfo.getIsPlaying()) {
                return i + 1 < videoList.size() ? videoList.get(i + 1) : null;
            }
        }
        return null;
    }



    @Override
    public String toString() {
        return " name= " + name
                + " displayName= " + displayName
                + " lastModify= " + lastModify
                + " createTime= " + createTime
                + " videosSizeSum= " + videosSizeSum
                + " mVideoListSize= " + (videoList == null ? -1 : videoList.size())
                + " path= " + path;
    }

    public String nameToString() {
        String nameArray = displayName + " " + getVideoCnt() + " videos : [";
        if (videoList != null && videoList.size() > 0) {
            for (VideoInfo video : videoList) {
                nameArray += video.getDisplayName() + " ";
            }
        }
        nameArray += "]";
        return nameArray;
    }
}
