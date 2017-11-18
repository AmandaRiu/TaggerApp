package com.amandariu.tagger.demo.data.source.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * @author amandariu (11/12/17)
 */
@Database(entities = {TagEntity.class}, version = 1)
public abstract class TagDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "tags.db";

    public abstract TagDao tagDao();
}
