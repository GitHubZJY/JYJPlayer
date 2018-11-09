package com.jyj.video.jyjplayer.db.bean;



import com.google.gson.reflect.TypeToken;
import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * @author denglongyun
 * @date 17-11-7
 */
@Entity
public class Folder {


    @Id
    @NotNull
    @Unique
    private String path;
    private String name;
    private String displayName;
    private long videosSizeSum;
    private int type = InfoType.DIRECTORY;
    private boolean isNew = false;
    private long lastModify = 0L; // 创建或者最后修改时间
    private String videos;
    private long createTime = 0L; // 创建时间


    //----------------------------------setter getter 分隔线----------------------------------------//

    public String getVideos() {
        return this.videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public long getLastModify() {
        return this.lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getVideosSizeSum() {
        return this.videosSizeSum;
    }

    public void setVideosSizeSum(long videosSizeSum) {
        this.videosSizeSum = videosSizeSum;
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

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Generated(hash = 1986637154)
    public Folder(@NotNull String path, String name, String displayName, long videosSizeSum, int type,
            boolean isNew, long lastModify, String videos, long createTime) {
        this.path = path;
        this.name = name;
        this.displayName = displayName;
        this.videosSizeSum = videosSizeSum;
        this.type = type;
        this.isNew = isNew;
        this.lastModify = lastModify;
        this.videos = videos;
        this.createTime = createTime;
    }

    @Generated(hash = 1947132626)
    public Folder() {
    }


    public Folder(@NotNull FolderInfo folderInfo) {
        this.path = folderInfo.getPath();
        this.name = folderInfo.getName();
        this.displayName = folderInfo.getDisplayName();
        this.videosSizeSum = folderInfo.getSize();
        this.type = folderInfo.getType();
        this.isNew = folderInfo.getIsNew();
        this.lastModify = folderInfo.getLastModify();
        this.videos = AppApplication.getGson().toJson(folderInfo.getVideoList());
        this.createTime = folderInfo.getCreateTime();
    }

    public static FolderInfo switchFolderInfo(@NotNull Folder folder) {
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.setPath(folder.getPath());
        folderInfo.setName(folder.getName());
        folderInfo.setDisplayName(folder.getDisplayName());
        folderInfo.setSize(folder.getVideosSizeSum());
        folderInfo.setType(folder.getType());
        folderInfo.setIsNew(folder.getIsNew());
        folderInfo.setLastModify(folder.getLastModify());
        folderInfo.setCreateTime(folder.getCreateTime());
        folderInfo.setVideoList(getVideoListFromJson(folder.videos));
        folderInfo.setCreateTime(folder.getCreateTime());

        return folderInfo;
    }

    public static List<VideoInfo> getVideoListFromJson(String videoJson) {
        return AppApplication.getGson().fromJson(videoJson, new TypeToken<List<VideoInfo>>() {
        }.getType());
    }

}
