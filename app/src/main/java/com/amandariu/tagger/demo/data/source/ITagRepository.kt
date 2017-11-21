package com.amandariu.tagger.demo.data.source

/**
 * Interface for working with the Repository for this application.
 *
 * @author Amanda Riu
 */
interface ITagRepository : ISourceBase {
    /**
     * Instructs the Repository to purge it's in-memory cache and refresh
     * the tags.
     */
    fun refreshTags()

    /**
     * Fetch tags from the remote api data source. If tags are successfully fetched, the
     * cache will be updated, as well as the local data source. The provided callback will be
     * notified of the results.
     * @param callback The callback to send the results.
     */
    fun getTagsFromRemoteDataSource(callback: ISourceBase.ILoadTagsCallback)

    /**
     * Fetch tags from the local datasource. If tags are successfully loaded, the cache will
     * be updated. The provided callback will be notified of the results.
     * @param callback The callback to send the results.
     */
    fun getTagsFromLocalDataSource(callback: ISourceBase.ILoadTagsCallback)
}