package com.jyj.video.jyjplayer.db.helper;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.db.dao.DaoMaster;
import com.jyj.video.jyjplayer.db.dao.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * @author denglongyun
 * @date 17-11-7
 */

public abstract class BaseHelper {

    protected static final String LOG_TAG = "DAO";

    static {
        QueryBuilder.LOG_SQL = false;
    }

    public static final String VIDEO_DATA_DB = "VIDEODATABASE.db";

    private static DaoMaster sDaoMaster;
    private static DaoSession sDaoSession;

    private static DaoMaster getDaoMaster() {
        if (sDaoMaster == null) {
            DBOpenHelper helper = new DBOpenHelper(AppApplication.getContext(), VIDEO_DATA_DB);
            sDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    protected static DaoSession getDaoSession() {
        if (sDaoSession == null) {
            sDaoSession = getDaoMaster().newSession();
        }
        return sDaoSession;
    }
}
