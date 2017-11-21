package com.amandariu.tagger.demo.data.source

import com.amandariu.tagger.ITag

/**
 * Main entry point for accessing Tags data.
 */
interface ITagsDataSource : ISourceBase {

    interface ISaveTagsCallback {
        fun onTagsSavedSuccess()
        fun onTagsSavedError(e: Exception)
    }

    fun saveTags(tags: List<ITag>, callback: ISaveTagsCallback)
}
