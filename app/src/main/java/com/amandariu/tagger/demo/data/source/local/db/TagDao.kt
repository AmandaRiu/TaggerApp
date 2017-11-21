package com.amandariu.tagger.demo.data.source.local.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 * Dao for working with the tags table.
 *
 * @author Amanda Riu
 */
@Dao
interface TagDao {
    @Query("select * from tags")
    fun loadAllTags(): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(tags: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun save(tag: TagEntity)
}
