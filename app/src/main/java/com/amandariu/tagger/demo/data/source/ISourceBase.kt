package com.amandariu.tagger.demo.data.source

import com.amandariu.tagger.ITag

/**
 * Base interface for datasources.
 *
 * @author Amanda Riu
 */
interface ISourceBase {
    /**
     * Methods required for working with the TagsDataSource.
     */
    interface ILoadTagsCallback {
        /**
         * Tags have been loaded from a datasource.
         * @param tags The tags pulled from the datasource.
         */
        fun onTagsLoaded(tags: List<ITag>)

        /**
         * No tags available for loading.
         */
        fun onDataNotAvailable(msg: String)
    }

    /**
     * Load tags from one or more datasources.
     * @param callback The callback to send the loaded tags to.
     */
    fun getTags(callback: ILoadTagsCallback)

    /**
     * Gracefully shutdown and release references.
     */
    fun shutdown()
}
