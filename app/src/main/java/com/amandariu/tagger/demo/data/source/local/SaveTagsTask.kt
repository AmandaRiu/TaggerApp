package com.amandariu.tagger.demo.data.source.local

import android.os.AsyncTask
import android.util.Log

import com.amandariu.tagger.demo.data.source.local.db.TagDatabase
import com.amandariu.tagger.demo.data.source.local.db.TagEntity

import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

import com.amandariu.tagger.demo.data.source.ITagsDataSource.ISaveTagsCallback

/**
 * Save tags to the database for offline use.
 *
 * @author Amanda Riu
 */
internal class SaveTagsTask(private val mDb: TagDatabase,
                            private val mTags: List<TagEntity>,
                            callback: ISaveTagsCallback) : AsyncTask<Void, Void, Void>() {

    private val TAG = SaveTagsTask::class.java.simpleName
    private val mCallback: WeakReference<ISaveTagsCallback>

    init {
        mCallback = WeakReference(callback)
    }

    override fun doInBackground(vararg voids: Void): Void? {

        val startTime = System.nanoTime()
        try {
            mDb.tagDao().insertAll(mTags)
            val elapsedTime = System.nanoTime() - startTime
            Log.d(TAG, "Total time to save Tags to database ["
                    + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds")
            val callback = mCallback.get()
            callback?.onTagsSavedSuccess()
        } catch (e: Exception) {
            val callback = mCallback.get()
            callback?.onTagsSavedError(e)
        }
        return null
    }
}
