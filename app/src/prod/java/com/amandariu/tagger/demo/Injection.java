package com.amandariu.tagger.demo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.amandariu.tagger.demo.data.source.ITagsDataSource;
import com.amandariu.tagger.demo.data.source.TagsRepository;
import com.amandariu.tagger.demo.data.source.local.TagsLocalDataSource;
import com.amandariu.tagger.demo.data.source.remote.TagsRemoteDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of production implementations of the
 * {@link ITagsDataSource} at compile time.
 *
 * @author Amanda Riu
 */
public class Injection {

    public static TagsRepository provideTagsRepository(@NonNull Context context) {
        checkNotNull(context);
        return TagsRepository.Companion.getInstance(new TagsRemoteDataSource(), new TagsLocalDataSource());
    }
}
