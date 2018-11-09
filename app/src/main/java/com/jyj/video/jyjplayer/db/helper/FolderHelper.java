package com.jyj.video.jyjplayer.db.helper;

import com.jyj.video.jyjplayer.db.bean.Folder;
import com.jyj.video.jyjplayer.db.dao.FolderDao;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹数据的db操作类
 * @author denglongyun
 * @date 17-11-7
 */

public class FolderHelper extends BaseHelper {


    private static FolderHelper sInstance;

    public static FolderHelper getInstance() {
        if (sInstance == null) {
            sInstance = new FolderHelper();
        }
        sInstance.mDao = getDaoSession().getFolderDao();
        return sInstance;
    }

    private FolderDao mDao;

    private FolderHelper() {
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull FolderInfo video) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(new Folder(video));
    }

    public Folder queryByPath(String path) {
        if (mDao == null) {
            return null;
        }
        return mDao.queryBuilder().where(FolderDao.Properties.Path.eq(path))
                .build().unique();
    }

    public FolderInfo queryFolderInfoByPath(String path) {
        Folder f = queryByPath(path);
        if (f != null) {
            return Folder.switchFolderInfo(f);
        }
        return null;
    }


    public List<Folder> queryFolderDaoData() {
        if (mDao == null) {
            return null;
        }
        try {
            return mDao.queryBuilder().where(FolderDao.Properties.Path.notEq(""))
                    .orderDesc(FolderDao.Properties.LastModify)
                    .build().list();
        } catch (Throwable t) {
            // IllegalStateException: Couldn't read row 0, col 0 from CursorWindow.
            // Android SQLite returns rows in cursor windows that have the maximum size of 2MB as specified by config_cursorWindowSize.
            // If your row exceeds this limit, you'll get this error.
            t.printStackTrace();
            return null;
        }
    }

    public List<FolderInfo> query() {
        List<FolderInfo> resultList = new ArrayList<FolderInfo>();
        List<Folder> folderDatas= queryFolderDaoData();
        if (folderDatas == null || folderDatas.isEmpty()) {
            return resultList;
        }
        for (Folder f : folderDatas) {
            resultList.add(Folder.switchFolderInfo(f));
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
