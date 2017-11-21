package com.amandariu.tagger.demo.data.source

import android.content.Context
import android.util.Log
import com.amandariu.tagger.ITag
import com.amandariu.tagger.demo.TaggerApplication
import com.amandariu.tagger.demo.utils.isNetworkConnected
import java.util.ArrayList
import com.google.common.base.Preconditions.checkNotNull
import com.amandariu.tagger.demo.data.source.ISourceBase.ILoadTagsCallback
/**
 * Centralized gateway for working with remote and local repositories.
 *
 * @author Amanda Riu
 */
class TagsRepository
/**
 * Singleton class. Prevent remote instantiation.
 *
 * @param [mTagsRemoteDataSource] The datasource for connecting to the remote tags API.
 * @param [mTagsLocalDataSource] The datasource for connecting to the local tags database.
 */
private constructor(private var mTagsRemoteDataSource: ITagsDataSource?,
                    private var mTagsLocalDataSource: ITagsDataSource?) : ITagRepository {

    companion object {
        private val TAG = TagsRepository::class.java.simpleName
        private var sInstance: TagsRepository? = null

        /**
         * Returns the single instance of this Singleton, creating it if necessary.
         *
         * @param remoteDatasource The remote data source.
         * @return The [TagsRepository] instance.
         */
        fun getInstance(remoteDatasource: ITagsDataSource,
                        localDatasource: ITagsDataSource): TagsRepository {
            if (sInstance == null) {
                sInstance = TagsRepository(remoteDatasource, localDatasource)
            }
            return sInstance!!
        }
    }

    private var mIsFetchingTags = false

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    private var mCachedTags: MutableList<ITag>? = null

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This
     * variable has package local visibility so it can be accessed from tests.
     */
    private var mCacheIsDirty = false

    /**
     * Shut down this repository and all attached datasources.
     */
    override fun shutdown() {
        Log.v(TAG, "Shutting down TagRepository")

        if (mTagsLocalDataSource != null) {
            mTagsLocalDataSource!!.shutdown()
            mTagsLocalDataSource = null
        }
        if (mTagsRemoteDataSource != null) {
            mTagsRemoteDataSource!!.shutdown()
            mTagsRemoteDataSource = null
        }
        sInstance = null
    }

    /**
     * Load tags from cache, local or remote data sources, which ever is available first.
     * Note: [ILoadTagsCallback.onDataNotAvailable] is fired if all data sources
     * fail to get the data.
     *
     * @param callback The callback to send the loaded tags to.
     */
    override fun getTags(callback: ISourceBase.ILoadTagsCallback) {

        if (mIsFetchingTags) {
            Log.w(TAG, "TagsRepository is already fetching tags...do nothing.")
            return // already actively pulling tags.
        }

        checkNotNull(callback)
        if (!mCacheIsDirty && mCachedTags != null && mCachedTags!!.size > 0) {
            //
            // Return cached tags
            Log.d(TAG, "Returning tags from cache")
            mIsFetchingTags = false
            mCachedTags?.let {
                callback.onTagsLoaded(it)
            }
        } else {
            //
            // If network connected, pull the tags from the remote api to
            // refresh the database.
            val ctx: Context = TaggerApplication.instance!!.applicationContext
            if (isNetworkConnected(ctx)) {
                //
                // Grab tags from remote api.
                Log.d(TAG, "Fetching tags from the remote API")
                getTagsFromRemoteDataSource(callback)
            } else {
                //
                // Load tags from the database
                Log.d(TAG, "Device not connected, loading tags from database")
                getTagsFromLocalDataSource(callback)
            }
        }
    }

    /**
     * Set the dirty bit to true so the cached tags will be refreshed next time
     * they are requested.
     */
    override fun refreshTags() {
        mCacheIsDirty = true
    }

    /**
     * Fetch tags from the remote api data source. If tags are successfully fetched, the
     * cache will be updated, as well as the local data source. The provided callback will be
     * notified of the results.
     *
     * @param callback The callback to send the results.
     */
    override fun getTagsFromRemoteDataSource(callback: ISourceBase.ILoadTagsCallback) {
        mTagsRemoteDataSource!!.getTags(object : ISourceBase.ILoadTagsCallback {
            override fun onTagsLoaded(tags: List<ITag>) {
                refreshCache(tags)
                refreshLocalDataSource(tags)
                callback.onTagsLoaded(tags)
                mIsFetchingTags = false
            }

            override fun onDataNotAvailable(msg: String) {
                callback.onDataNotAvailable(msg)
                mIsFetchingTags = false
            }
        })
    }

    /**
     * Fetch tags from the local datasource. If tags are successfully loaded, the cache will
     * be updated. The provided callback will be notified of the results.
     * @param callback The callback to send the results.
     */
    override fun getTagsFromLocalDataSource(callback: ISourceBase.ILoadTagsCallback) {
        mTagsLocalDataSource!!.getTags(object : ISourceBase.ILoadTagsCallback {
            override fun onTagsLoaded(tags: List<ITag>) {
                refreshCache(tags)
                callback.onTagsLoaded(tags)
                mIsFetchingTags = false
            }

            override fun onDataNotAvailable(msg: String) {
                callback.onDataNotAvailable(msg)
                mIsFetchingTags = false
            }
        })
    }

    /**
     * Updates the local cache with a fresh copy of all the tags.
     *
     * @param tags The tags to save to cache.
     */
    private fun refreshCache(tags: List<ITag>) {
        if (mCachedTags == null) {
            mCachedTags = ArrayList()
        }
        mCachedTags!!.clear()
        mCachedTags!!.addAll(tags)
        mCacheIsDirty = false
    }

    /**
     * Save the provided tags to the local data source.
     *
     * @param tags The tags to be saved to the local data source.
     */
    private fun refreshLocalDataSource(tags: List<ITag>) {
        if (mTagsLocalDataSource != null) {
            mTagsLocalDataSource!!.saveTags(tags, object : ITagsDataSource.ISaveTagsCallback {
                override fun onTagsSavedSuccess() {
                    Log.d(TAG, "Tags successfully saved to the local database")
                }

                override fun onTagsSavedError(e: Exception) {
                    Log.e(TAG, "Error saving tags to the local database", e)
                }
            })
        }
    }
}
