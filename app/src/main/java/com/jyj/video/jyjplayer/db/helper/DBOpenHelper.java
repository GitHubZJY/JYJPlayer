package com.jyj.video.jyjplayer.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jyj.video.jyjplayer.db.dao.DaoMaster;
import com.jyj.video.jyjplayer.db.dao.DownFilmDao;
import com.jyj.video.jyjplayer.db.dao.DownloadDao;
import com.jyj.video.jyjplayer.db.dao.DurationDao;
import com.jyj.video.jyjplayer.db.dao.SubtitleDao;
import com.jyj.video.jyjplayer.db.dao.ThumbnailDao;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.greendao.database.Database;


/**
 * 默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，导致数据的丢失。
 * 封装一层 自定义升级方式
 * @author denglongyun
 * @date 17-11-7
 */

public class DBOpenHelper extends DaoMaster.DevOpenHelper {

    public DBOpenHelper(Context context, String name) {
        super(context, name);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        LogUtil.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
        // 自定义升级方式
//        onCreate(db);
        // TODO 升级时需要的表处理
//        VideoDao.createTable(db, true);
//        FolderDao.createTable(db, true);
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE FOLDER ADD COLUMN CREATE_TIME INTEGER NOT NULL DEFAULT 0;");
            db.execSQL("ALTER TABLE VIDEO ADD COLUMN CREATE_TIME INTEGER NOT NULL DEFAULT 0;");
            ThumbnailDao.createTable(db, true);
            DurationDao.createTable(db, true);
        }
        if (oldVersion < 3) {
            SubtitleDao.createTable(db, true);
        }
        if (oldVersion < 4) {
            DownloadDao.createTable(db, true);
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE DOWNLOAD ADD COLUMN SIDECARS TEXT;");
            db.execSQL("ALTER TABLE DOWNLOAD ADD COLUMN LAST_FINISH_PATH TEXT;");
            db.execSQL("ALTER TABLE DOWNLOAD ADD COLUMN COMPLETENESS TEXT;");
            db.execSQL("ALTER TABLE DOWNLOAD ADD COLUMN IS_VIDEO INTEGER NOT NULL DEFAULT 0;");
        }
        if(oldVersion < 6){
            DownFilmDao.createTable(db, true);
        }
        if(oldVersion < 7){
            db.execSQL("ALTER TABLE DOWN_FILM ADD COLUMN WEB_VIEW_URL STRING;");
        }
    }

}
