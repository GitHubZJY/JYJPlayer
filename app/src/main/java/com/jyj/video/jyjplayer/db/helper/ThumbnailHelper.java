package com.jyj.video.jyjplayer.db.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jyj.video.jyjplayer.db.bean.Thumbnail;
import com.jyj.video.jyjplayer.db.dao.ThumbnailDao;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.ByteArrayOutputStream;

/**
 * 视频数据的db操作类
 * @author denglongyun
 * @date 17-11-7
 */

public class ThumbnailHelper extends BaseHelper {


    private static ThumbnailHelper sInstance;

    public static ThumbnailHelper getInstance() {
        if (sInstance == null) {
            sInstance = new ThumbnailHelper();
        }
        sInstance.mDao = getDaoSession().getThumbnailDao();
        return sInstance;
    }

    private ThumbnailDao mDao;

    private ThumbnailHelper() {
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull Thumbnail thumbnail) {
        if (mDao == null) {
            return -1;
        }
        return mDao.insertOrReplace(thumbnail);
    }

    /**
     * add or update
     */
    public long addOrReplace(@NotNull String path, @NotNull Bitmap thumbnail) {
        if (thumbnail == null || mDao == null) {
            return -1;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, os);
        return mDao.insertOrReplace(new Thumbnail(path, os.toByteArray()));
    }

    public Bitmap queryByPath(String path) {
        if (mDao == null) {
            return null;
        }
        Thumbnail thumb = mDao.queryBuilder().where(ThumbnailDao.Properties.Path.eq(path)).build().unique();
        if (thumb == null) {
            return null;
        }
        byte[] thumbnail = thumb.getThumbnail();
        if (thumbnail == null || thumbnail.length < 1) {
            return null;
        }
        return BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length, null);
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
