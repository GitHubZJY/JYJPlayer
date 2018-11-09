package com.jyj.video.jyjplayer.manager;

import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;

/**
 * Created by 74215 on 2018/11/7.
 */

public class VideoPlayDataManager {

    private VideoInfo mCurPlayVideoInfo;

    private static VideoPlayDataManager manager;

    public static VideoPlayDataManager getInstance(){
        if(manager == null){
            manager = new VideoPlayDataManager();
        }
        return manager;
    }


    public VideoInfo getCurPlayVideoInfo(){
        return mCurPlayVideoInfo;
    }

    public void setCurPlayVideoInfo(VideoInfo videoInfo){
        mCurPlayVideoInfo = videoInfo;
    }
}
