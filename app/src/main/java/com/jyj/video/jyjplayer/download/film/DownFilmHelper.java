package com.jyj.video.jyjplayer.download.film;

import com.jyj.video.jyjplayer.db.bean.DownFilm;
import com.jyj.video.jyjplayer.db.dao.DownFilmDao;
import com.jyj.video.jyjplayer.db.helper.BaseHelper;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjiayang on 2018/5/8.
 */

public class DownFilmHelper extends BaseHelper {


    private static DownFilmHelper sInstance;

    public static DownFilmHelper getInstance() {
        if (sInstance == null) {
            sInstance = new DownFilmHelper();
        }
        sInstance.mDao = getDaoSession().getDownFilmDao();
        return sInstance;
    }

    private DownFilmDao mDao;

    private DownFilmHelper() {
    }

    /**
     * add or update
     */
    private long addOrReplace(@NotNull DownFilm info) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(info);
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull DownLoadFilmInfo info) {
        return addOrReplace(new DownFilm(info));
    }

    private DownFilm queryByUrl(String url) {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(DownFilmDao.Properties.Url.eq(url))
                .build().unique();
    }

    public DownLoadFilmInfo queryInfoByUrl(String url) {
        return DownFilm.switchDownloadInfo(queryByUrl(url));
    }

    private List<DownFilm> queryAll() {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(DownFilmDao.Properties.Url.notEq(""))
                .build().list();
    }

    public List<DownLoadFilmInfo> query() {
        List<DownLoadFilmInfo> resultList = new ArrayList<DownLoadFilmInfo>();
        List<DownFilm> folderDatas= queryAll();
        if (folderDatas == null || folderDatas.isEmpty()) {
            return resultList;
        }
        for (DownFilm f : folderDatas) {
            resultList.add(DownFilm.switchDownloadInfo(f));
        }
        return resultList;
    }

    public void deleteByUrl(String url) {
        if (mDao == null) {
            return;
        }
        mDao.deleteByKey(url);
    }

    public void deleteAll() {
        if (mDao == null) {
            return;
        }
        mDao.deleteAll();
    }


}
