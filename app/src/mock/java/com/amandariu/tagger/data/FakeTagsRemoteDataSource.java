package com.amandariu.tagger.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.amandariu.tagger.demo.data.Tag;
import com.amandariu.tagger.demo.data.source.TagsDataSource;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a remote datasource for easy testing.
 *
 * @author Amanda Riu
 */
public class FakeTagsRemoteDataSource implements TagsDataSource {

    private static FakeTagsRemoteDataSource sInstance = null;
    private static final List<Tag> TAGS_DATA_SERVICE = new ArrayList<>();


    public static FakeTagsRemoteDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new FakeTagsRemoteDataSource();
        }
        return sInstance;
    }

    /**
     * Load tags from one or more datasources.
     *
     * @param callback The callback to send the loaded tags to.
     */
    @Override
    public void getTags(@NonNull LoadTagsCallback callback) {
        callback.onTagsLoaded(Lists.newArrayList(TAGS_DATA_SERVICE));
    }

    /**
     *
     */
    @Override
    public void refreshTags() {
        //
        // Not needed.
    }

    @VisibleForTesting
    public void addTags(Tag... tags) {
        if (tags != null) {
            for (Tag tag : tags) {
                TAGS_DATA_SERVICE.add(tag);
            }
        }
    }
}
