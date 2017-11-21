package com.amandariu.tagger.demo.data.source.local

import android.os.AsyncTask
import android.util.Log

import com.amandariu.tagger.demo.data.source.local.db.TagConverter
import com.amandariu.tagger.demo.data.source.local.db.TagDatabase
import com.amandariu.tagger.demo.data.source.local.db.TagEntity
import java.util.concurrent.TimeUnit

import com.amandariu.tagger.demo.data.source.ISourceBase.ILoadTagsCallback

/**
 * Fetches tags from the Tag Database.
 *
 * @author Amanda Riu
 */
internal class GetTagsTask(private val mDb: TagDatabase,
                           private val mCallback: ILoadTagsCallback)
    : AsyncTask<Void, Void, List<TagEntity>>() {

    companion object {
        private val TAG = GetTagsTask::class.java.simpleName
    }

    override fun doInBackground(vararg voids: Void): List<TagEntity> {
        val startTime = System.nanoTime()
        val tags = mDb.tagDao().loadAllTags()
        val elapsedTime = System.nanoTime() - startTime
        Log.d(TAG, "Total time to get tags from database ["
                + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds")
        return tags
    }

    override fun onPostExecute(tags: List<TagEntity>) {
        val newTags = TagConverter.toTagList(tags)
        mCallback.onTagsLoaded(newTags)
    }
}
