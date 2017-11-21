package com.amandariu.tagger.demo.data.source.local.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Tags Database for saving tags fetched from the network api
 *
 * @author Amanda Riu
 */
@Database(entities = arrayOf(TagEntity::class), version = 1)
abstract class TagDatabase : RoomDatabase() {

    companion object {
        val DATABASE_NAME = "tags.db"
    }

    abstract fun tagDao(): TagDao
}
