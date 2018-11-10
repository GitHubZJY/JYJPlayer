package com.jyj.video.jyjplayer.filescan.model.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjyang
 * @date 17-12-8
 */

public class SubtitleInfo extends AbstractBean {

    private String videoPath;
    private SubtitleBase curSubtitle;
    private List<SubtitleBase> allSubtitles = new ArrayList<SubtitleBase>();


    public String getVideoPath() {
        return this.videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public SubtitleBase getCurSubtitle() {
        return this.curSubtitle;
    }

    public void setCurSubtitle(SubtitleBase info) {
        this.curSubtitle = info;
        addSubtitle(info);
    }


    public List<SubtitleBase> getAllSubtitles() {
        return this.allSubtitles;
    }

    public void setAllSubtitles(List<SubtitleBase> list) {
        if (list != null && !list.isEmpty()) {
            if (allSubtitles != null) {
                this.allSubtitles.clear();
            } else {
                this.allSubtitles = new ArrayList<SubtitleBase>();
            }
            this.allSubtitles.addAll(list);
        }
        this.allSubtitles = list;
    }

    /**
     * 添加单个字幕数据到列表
     * @param base
     */
    public void addSubtitle(SubtitleBase base) {
        if (base == null || TextUtils.isEmpty(base.getPath())) {
            return;
        }
        boolean isUpdate = false;
        if (allSubtitles == null) {
            allSubtitles = new ArrayList<SubtitleBase>();
        }
        if (allSubtitles.isEmpty()) {
            allSubtitles.add(base);
        } else {
            for (int i=0; i<allSubtitles.size(); i++) {
                SubtitleBase s = allSubtitles.get(i);
                String path = s.getPath();
                if (!TextUtils.isEmpty(path) && path.equals(base.getPath())) {
                    allSubtitles.set(i, base);
                    isUpdate = true;
                    //allSubtitles.remove(s);
                    break;
                }
            }
            if(!isUpdate){
                allSubtitles.add(base);
            }
        }

    }
}
