package com.jyj.video.jyjplayer.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.jyj.video.jyjplayer.db.bean.Video;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "VIDEO".
*/
public class VideoDao extends AbstractDao<Video, String> {

    public static final String TABLENAME = "VIDEO";

    /**
     * Properties of entity Video.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Path = new Property(0, String.class, "path", true, "PATH");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property DisplayName = new Property(2, String.class, "displayName", false, "DISPLAY_NAME");
        public final static Property ParentFileName = new Property(3, String.class, "parentFileName", false, "PARENT_FILE_NAME");
        public final static Property Size = new Property(4, long.class, "size", false, "SIZE");
        public final static Property Duration = new Property(5, long.class, "duration", false, "DURATION");
        public final static Property SubtitlePath = new Property(6, String.class, "subtitlePath", false, "SUBTITLE_PATH");
        public final static Property SubtitleName = new Property(7, String.class, "subtitleName", false, "SUBTITLE_NAME");
        public final static Property LastModify = new Property(8, long.class, "lastModify", false, "LAST_MODIFY");
        public final static Property IsNew = new Property(9, boolean.class, "isNew", false, "IS_NEW");
        public final static Property CreateTime = new Property(10, long.class, "createTime", false, "CREATE_TIME");
    }


    public VideoDao(DaoConfig config) {
        super(config);
    }
    
    public VideoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VIDEO\" (" + //
                "\"PATH\" TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: path
                "\"NAME\" TEXT," + // 1: name
                "\"DISPLAY_NAME\" TEXT," + // 2: displayName
                "\"PARENT_FILE_NAME\" TEXT," + // 3: parentFileName
                "\"SIZE\" INTEGER NOT NULL ," + // 4: size
                "\"DURATION\" INTEGER NOT NULL ," + // 5: duration
                "\"SUBTITLE_PATH\" TEXT," + // 6: subtitlePath
                "\"SUBTITLE_NAME\" TEXT," + // 7: subtitleName
                "\"LAST_MODIFY\" INTEGER NOT NULL ," + // 8: lastModify
                "\"IS_NEW\" INTEGER NOT NULL ," + // 9: isNew
                "\"CREATE_TIME\" INTEGER NOT NULL );"); // 10: createTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VIDEO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Video entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getPath());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String displayName = entity.getDisplayName();
        if (displayName != null) {
            stmt.bindString(3, displayName);
        }
 
        String parentFileName = entity.getParentFileName();
        if (parentFileName != null) {
            stmt.bindString(4, parentFileName);
        }
        stmt.bindLong(5, entity.getSize());
        stmt.bindLong(6, entity.getDuration());
 
        String subtitlePath = entity.getSubtitlePath();
        if (subtitlePath != null) {
            stmt.bindString(7, subtitlePath);
        }
 
        String subtitleName = entity.getSubtitleName();
        if (subtitleName != null) {
            stmt.bindString(8, subtitleName);
        }
        stmt.bindLong(9, entity.getLastModify());
        stmt.bindLong(10, entity.getIsNew() ? 1L: 0L);
        stmt.bindLong(11, entity.getCreateTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Video entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getPath());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String displayName = entity.getDisplayName();
        if (displayName != null) {
            stmt.bindString(3, displayName);
        }
 
        String parentFileName = entity.getParentFileName();
        if (parentFileName != null) {
            stmt.bindString(4, parentFileName);
        }
        stmt.bindLong(5, entity.getSize());
        stmt.bindLong(6, entity.getDuration());
 
        String subtitlePath = entity.getSubtitlePath();
        if (subtitlePath != null) {
            stmt.bindString(7, subtitlePath);
        }
 
        String subtitleName = entity.getSubtitleName();
        if (subtitleName != null) {
            stmt.bindString(8, subtitleName);
        }
        stmt.bindLong(9, entity.getLastModify());
        stmt.bindLong(10, entity.getIsNew() ? 1L: 0L);
        stmt.bindLong(11, entity.getCreateTime());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    @Override
    public Video readEntity(Cursor cursor, int offset) {
        Video entity = new Video( //
            cursor.getString(offset + 0), // path
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // displayName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // parentFileName
            cursor.getLong(offset + 4), // size
            cursor.getLong(offset + 5), // duration
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // subtitlePath
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // subtitleName
            cursor.getLong(offset + 8), // lastModify
            cursor.getShort(offset + 9) != 0, // isNew
            cursor.getLong(offset + 10) // createTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Video entity, int offset) {
        entity.setPath(cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDisplayName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setParentFileName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSize(cursor.getLong(offset + 4));
        entity.setDuration(cursor.getLong(offset + 5));
        entity.setSubtitlePath(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSubtitleName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setLastModify(cursor.getLong(offset + 8));
        entity.setIsNew(cursor.getShort(offset + 9) != 0);
        entity.setCreateTime(cursor.getLong(offset + 10));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Video entity, long rowId) {
        return entity.getPath();
    }
    
    @Override
    public String getKey(Video entity) {
        if(entity != null) {
            return entity.getPath();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Video entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
