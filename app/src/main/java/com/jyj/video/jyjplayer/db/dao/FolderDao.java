package com.jyj.video.jyjplayer.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.jyj.video.jyjplayer.db.bean.Folder;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "FOLDER".
*/
public class FolderDao extends AbstractDao<Folder, String> {

    public static final String TABLENAME = "FOLDER";

    /**
     * Properties of entity Folder.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Path = new Property(0, String.class, "path", true, "PATH");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property DisplayName = new Property(2, String.class, "displayName", false, "DISPLAY_NAME");
        public final static Property VideosSizeSum = new Property(3, long.class, "videosSizeSum", false, "VIDEOS_SIZE_SUM");
        public final static Property Type = new Property(4, int.class, "type", false, "TYPE");
        public final static Property IsNew = new Property(5, boolean.class, "isNew", false, "IS_NEW");
        public final static Property LastModify = new Property(6, long.class, "lastModify", false, "LAST_MODIFY");
        public final static Property Videos = new Property(7, String.class, "videos", false, "VIDEOS");
        public final static Property CreateTime = new Property(8, long.class, "createTime", false, "CREATE_TIME");
    }


    public FolderDao(DaoConfig config) {
        super(config);
    }
    
    public FolderDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FOLDER\" (" + //
                "\"PATH\" TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: path
                "\"NAME\" TEXT," + // 1: name
                "\"DISPLAY_NAME\" TEXT," + // 2: displayName
                "\"VIDEOS_SIZE_SUM\" INTEGER NOT NULL ," + // 3: videosSizeSum
                "\"TYPE\" INTEGER NOT NULL ," + // 4: type
                "\"IS_NEW\" INTEGER NOT NULL ," + // 5: isNew
                "\"LAST_MODIFY\" INTEGER NOT NULL ," + // 6: lastModify
                "\"VIDEOS\" TEXT," + // 7: videos
                "\"CREATE_TIME\" INTEGER NOT NULL );"); // 8: createTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FOLDER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Folder entity) {
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
        stmt.bindLong(4, entity.getVideosSizeSum());
        stmt.bindLong(5, entity.getType());
        stmt.bindLong(6, entity.getIsNew() ? 1L: 0L);
        stmt.bindLong(7, entity.getLastModify());
 
        String videos = entity.getVideos();
        if (videos != null) {
            stmt.bindString(8, videos);
        }
        stmt.bindLong(9, entity.getCreateTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Folder entity) {
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
        stmt.bindLong(4, entity.getVideosSizeSum());
        stmt.bindLong(5, entity.getType());
        stmt.bindLong(6, entity.getIsNew() ? 1L: 0L);
        stmt.bindLong(7, entity.getLastModify());
 
        String videos = entity.getVideos();
        if (videos != null) {
            stmt.bindString(8, videos);
        }
        stmt.bindLong(9, entity.getCreateTime());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    @Override
    public Folder readEntity(Cursor cursor, int offset) {
        Folder entity = new Folder( //
            cursor.getString(offset + 0), // path
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // displayName
            cursor.getLong(offset + 3), // videosSizeSum
            cursor.getInt(offset + 4), // type
            cursor.getShort(offset + 5) != 0, // isNew
            cursor.getLong(offset + 6), // lastModify
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // videos
            cursor.getLong(offset + 8) // createTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Folder entity, int offset) {
        entity.setPath(cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDisplayName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setVideosSizeSum(cursor.getLong(offset + 3));
        entity.setType(cursor.getInt(offset + 4));
        entity.setIsNew(cursor.getShort(offset + 5) != 0);
        entity.setLastModify(cursor.getLong(offset + 6));
        entity.setVideos(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setCreateTime(cursor.getLong(offset + 8));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Folder entity, long rowId) {
        return entity.getPath();
    }
    
    @Override
    public String getKey(Folder entity) {
        if(entity != null) {
            return entity.getPath();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Folder entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
