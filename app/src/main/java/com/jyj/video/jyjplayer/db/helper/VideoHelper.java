package com.jyj.video.jyjplayer.db.helper;

import com.jyj.video.jyjplayer.db.bean.Video;
import com.jyj.video.jyjplayer.db.dao.VideoDao;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频数据的db操作类
 * @author zjyang
 * @date 17-11-7
 */

public class VideoHelper extends BaseHelper {


    private static VideoHelper sInstance;

    public static VideoHelper getInstance() {
        if (sInstance == null) {
            sInstance = new VideoHelper();
        }
        sInstance.mDao = getDaoSession().getVideoDao();
        return sInstance;
    }

    private VideoDao mDao;

    private VideoHelper() {
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull VideoInfo video) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(new Video(video));
    }

    public Video queryByPath(String path) {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(VideoDao.Properties.Path.eq(path))
                .build().unique();
    }

    public VideoInfo queryVideoInfoByPath(String path) {
        Video v = queryByPath(path);
        if (v != null) {
            return Video.switchVideoInfo(v);
        }
        return null;
    }


    public List<Video> queryVideoDaoData() {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(VideoDao.Properties.Path.notEq(""))
                .orderDesc(VideoDao.Properties.LastModify)
                .build().list();
    }

    public List<VideoInfo> query() {
        List<VideoInfo> resultList = new ArrayList<VideoInfo>();
        List<Video> videoDatas= queryVideoDaoData();
        if (videoDatas == null || videoDatas.isEmpty()) {
            return resultList;
        }
        for (Video v : videoDatas) {
            resultList.add(Video.switchVideoInfo(v));
        }
        return resultList;
    }

    public void deleteByPath(String path) {
        if (mDao == null) {
            return;
        }
        mDao.deleteByKey(path);
    }

    public void deleteAll() {
        if (mDao == null) {
            return;
        }
        mDao.deleteAll();
    }



}
