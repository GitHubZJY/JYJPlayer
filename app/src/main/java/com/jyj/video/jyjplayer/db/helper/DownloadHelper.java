package com.jyj.video.jyjplayer.db.helper;

import com.jyj.video.jyjplayer.db.bean.Download;
import com.jyj.video.jyjplayer.db.dao.DownloadDao;
import com.jyj.video.jyjplayer.download.bean.DownloadInfo;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹数据的db操作类
 * @author denglongyun
 * @date 17-11-7
 */

public class DownloadHelper extends BaseHelper {


    private static DownloadHelper sInstance;

    public static DownloadHelper getInstance() {
        if (sInstance == null) {
            sInstance = new DownloadHelper();
        }
        sInstance.mDao = getDaoSession().getDownloadDao();
        return sInstance;
    }

    private DownloadDao mDao;

    private DownloadHelper() {
    }

    /**
     * add or update
     */
    private long addOrReplace(@NotNull Download info) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(info);
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull DownloadInfo info) {
        return addOrReplace(new Download(info));
    }

    private Download queryByUrl(String url) {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(DownloadDao.Properties.Url.eq(url))
                .build().unique();
    }

    public DownloadInfo queryInfoByUrl(String url) {
        return Download.switchDownloadInfo(queryByUrl(url));
    }

    private List<Download> queryAll() {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(DownloadDao.Properties.Url.notEq(""))
                .build().list();
    }

    public List<DownloadInfo> query() {
        List<DownloadInfo> resultList = new ArrayList<DownloadInfo>();
        List<Download> folderDatas= queryAll();
        if (folderDatas == null || folderDatas.isEmpty()) {
            return resultList;
        }
        for (Download f : folderDatas) {
            resultList.add(Download.switchDownloadInfo(f));
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
