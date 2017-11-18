package com.amandariu.tagger.demo.data.source.local.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Dao for working with the tags table.
 *
 * @author amandariu (11/12/17)
 */
@Dao
public interface TagDao {
    @Query("select * from tags")
    List<TagEntity> loadAllTags();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<TagEntity> tags);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void save(TagEntity tag);
}
