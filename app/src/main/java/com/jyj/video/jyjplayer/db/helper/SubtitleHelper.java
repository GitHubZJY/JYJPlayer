package com.jyj.video.jyjplayer.db.helper;

import com.jyj.video.jyjplayer.db.bean.Subtitle;
import com.jyj.video.jyjplayer.db.dao.SubtitleDao;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleInfo;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹数据的db操作类
 * @author denglongyun
 * @date 17-11-7
 */

public class SubtitleHelper extends BaseHelper {


    private static SubtitleHelper sInstance;

    public static SubtitleHelper getInstance() {
        if (sInstance == null) {
            sInstance = new SubtitleHelper();
        }
        sInstance.mDao = getDaoSession().getSubtitleDao();
        return sInstance;
    }

    private SubtitleDao mDao;

    private SubtitleHelper() {
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull SubtitleInfo video) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(new Subtitle(video));
    }

    private Subtitle queryDaoByVideoPath(String path) {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(SubtitleDao.Properties.VideoPath.eq(path))
                .build().unique();
    }

    public SubtitleInfo queryByVideoPath(String path) {
        Subtitle s = queryDaoByVideoPath(path);
        if (s != null) {
            return Subtitle.switchSubtitleInfo(s);
        }
        return null;
    }


    public List<Subtitle> querySubtitleDaoData() {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(SubtitleDao.Properties.VideoPath.notEq(""))
                .build().list();
    }

    public List<SubtitleInfo> query() {
        List<SubtitleInfo> resultList = new ArrayList<SubtitleInfo>();
        List<Subtitle> folderDatas= querySubtitleDaoData();
        if (folderDatas == null || folderDatas.isEmpty()) {
            return resultList;
        }
        for (Subtitle s : folderDatas) {
            resultList.add(Subtitle.switchSubtitleInfo(s));
        }
        return resultList;
    }

    public void deleteByVideoPath(String path) {
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
