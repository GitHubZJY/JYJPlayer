package com.jyj.video.jyjplayer.db.bean;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleBase;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleInfo;
import com.google.gson.reflect.TypeToken;

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
public class Subtitle {


    @Id
    @NotNull
    @Unique
    private String videoPath;
    private String curSubtitle;
    private String allSubtitles;


    @Generated(hash = 2054852790)
    public Subtitle(@NotNull String videoPath, String curSubtitle, String allSubtitles) {
        this.videoPath = videoPath;
        this.curSubtitle = curSubtitle;
        this.allSubtitles = allSubtitles;
    }

    @Generated(hash = 1071476920)
    public Subtitle() {
    }


    //----------------------------------setter getter 分隔线----------------------------------------//

    public String getAllSubtitles() {
        return this.allSubtitles;
    }

    public void setAllSubtitles(String allSubtitles) {
        this.allSubtitles = allSubtitles;
    }

    public String getVideoPath() {
        return this.videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCurSubtitle() {
        return this.curSubtitle;
    }

    public void setCurSubtitle(String curSubtitle) {
        this.curSubtitle = curSubtitle;
    }

    public Subtitle(@NotNull SubtitleInfo subtitleInfo) {
        this.videoPath = subtitleInfo.getVideoPath();
        this.curSubtitle = AppApplication.getGson().toJson(subtitleInfo.getCurSubtitle());
        this.allSubtitles = AppApplication.getGson().toJson(subtitleInfo.getAllSubtitles());
    }

    public static SubtitleInfo switchSubtitleInfo(@NotNull Subtitle folder) {
        SubtitleInfo info = new SubtitleInfo();
        info.setVideoPath(folder.getVideoPath());
        info.setCurSubtitle(getSubtitleFromJson(folder.getCurSubtitle()));
        info.setAllSubtitles(getAllSubtitleListFromJson(folder.getAllSubtitles()));

        return info;
    }

    public static SubtitleBase getSubtitleFromJson(String videoJson) {
        return AppApplication.getGson().fromJson(videoJson, SubtitleBase.class);
    }

    public static List<SubtitleBase> getAllSubtitleListFromJson(String videoJson) {
        return AppApplication.getGson().fromJson(videoJson, new TypeToken<List<SubtitleBase>>() {
        }.getType());
    }

}
