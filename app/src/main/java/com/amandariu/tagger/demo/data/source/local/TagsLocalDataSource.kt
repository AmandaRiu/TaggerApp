package com.amandariu.tagger.demo.data.source.local

import android.arch.persistence.room.Room
import android.util.Log

import com.amandariu.tagger.ITag
import com.amandariu.tagger.demo.TaggerApplication
import com.amandariu.tagger.demo.data.source.ISourceBase
import com.amandariu.tagger.demo.data.source.ITagsDataSource
import com.amandariu.tagger.demo.data.source.local.db.TagConverter
import com.amandariu.tagger.demo.data.source.local.db.TagDatabase
import java.util.concurrent.TimeUnit

/**
 * Manages the Tags Database.
 *
 * @author Amanda Riu
 */
class TagsLocalDataSource : ITagsDataSource {

    companion object {
        private val TAG = TagsLocalDataSource::class.java.simpleName
    }

    private var mDb: TagDatabase? = null

    init {
        Log.v(TAG, "Initializing the local Tag Datasource")
        val startTime = System.nanoTime()
        mDb = Room.databaseBuilder(
                TaggerApplication.instance!!.applicationContext,
                TagDatabase::class.java,
                TagDatabase.DATABASE_NAME)
                .build()
        val elapsedTime = System.nanoTime() - startTime
        Log.d(TAG, "Total time to initialize database ["
                + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds")
    }

    /**
     * Load tags from one or more data sources.
     * @param [callback] The callback to send the loaded tags to.
     */
    override fun getTags(callback: ISourceBase.ILoadTagsCallback) {
        Log.d(TAG, "Processing request to load tags from DB")
        GetTagsTask(mDb!!, callback).execute()
    }

    /**
     * Save tags to the local database.
     * @param [tags] The list of tags to write to the database.
     * @param [callback] The callback to notify of result.
     */
    override fun saveTags(tags: List<ITag>,
                          callback: ITagsDataSource.ISaveTagsCallback) {
        Log.d(TAG, "Processing request to save ["
                + tags.size + "] tags to the db")
        val tagEntities = TagConverter.toTagEntityList(tags)
        SaveTagsTask(mDb!!, tagEntities, callback).execute()
    }

    /**
     * Shutdown the database.
     */
    override fun shutdown() {
        Log.v(TAG, "Shutting down the local tags datasource")
        if (mDb != null) {
            mDb!!.close()
        }
        mDb = null
    }
}
