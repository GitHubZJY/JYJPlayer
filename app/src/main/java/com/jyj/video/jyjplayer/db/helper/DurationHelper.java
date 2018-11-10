package com.jyj.video.jyjplayer.db.helper;

import com.jyj.video.jyjplayer.db.bean.Duration;
import com.jyj.video.jyjplayer.db.dao.DurationDao;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * 视频数据的db操作类
 * @author zjyang
 * @date 17-11-7
 */

public class DurationHelper extends BaseHelper {


    private static DurationHelper sInstance;

    public static DurationHelper getInstance() {
        if (sInstance == null) {
            sInstance = new DurationHelper();
        }
        sInstance.mDao = getDaoSession().getDurationDao();
        return sInstance;
    }

    private DurationDao mDao;

    private DurationHelper() {
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull Duration thumbnail) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(thumbnail);
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull String path, long duration) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(new Duration(path, duration));
    }

    public long queryByPath(String path) {
        if (mDao == null) {
            return 0L;
        }
        Duration duration = mDao.queryBuilder().where(DurationDao.Properties.Path.eq(path)).build().unique();
        if (duration == null) {
            return 0L;
        }
        return duration.getDuration();
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
