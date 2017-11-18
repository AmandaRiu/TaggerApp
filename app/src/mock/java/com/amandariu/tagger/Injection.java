package com.amandariu.tagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.amandariu.tagger.demo.data.FakeTagsRemoteDataSource;
import com.amandariu.tagger.demo.data.source.TagsDataSource;
import com.amandariu.tagger.demo.data.source.TagsRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of test implementations of the
 * {@link TagsDataSource} at compile time.
 *
 * @author Amanda Riu
 */
public class Injection {

    public static TagsRepository provideTagsRepository(@NonNull Context context) {
        checkNotNull(context);
        return TagsRepository.getInstance(FakeTagsRemoteDataSource.getInstance());
    }
}
