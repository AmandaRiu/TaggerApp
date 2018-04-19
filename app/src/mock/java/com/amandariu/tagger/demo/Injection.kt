package com.amandariu.tagger.demo

import android.content.Context
import com.amandariu.tagger.data.FakeTagsRemoteDataSource
import com.amandariu.tagger.demo.data.source.TagsRepository

/**
 * Enables injection of production implementations of the
 * [com.amandariu.tagger.demo.data.source.ITagsDataSource] at compile time.
 *
 * @author Amanda Riu
 */
object Injection {

    fun provideTagsRepository(context: Context): TagsRepository {
        checkNotNull(context)
        return TagsRepository.getInstance(
                FakeTagsRemoteDataSource.getInstance(), FakeTagsRemoteDataSource.getInstance())
    }
}