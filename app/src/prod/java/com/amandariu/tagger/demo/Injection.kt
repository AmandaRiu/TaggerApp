package com.amandariu.tagger.demo

import android.content.Context

import com.amandariu.tagger.demo.data.source.ITagsDataSource
import com.amandariu.tagger.demo.data.source.TagsRepository
import com.amandariu.tagger.demo.data.source.local.TagsLocalDataSource
import com.amandariu.tagger.demo.data.source.remote.TagsRemoteDataSource

import com.google.common.base.Preconditions.checkNotNull

/**
 * Enables injection of production implementations of the
 * [ITagsDataSource] at compile time.
 *
 * @author Amanda Riu
 */
object Injection {

    fun provideTagsRepository(context: Context): TagsRepository {
        checkNotNull(context)
        return TagsRepository.getInstance(TagsRemoteDataSource(), TagsLocalDataSource())
    }
}
