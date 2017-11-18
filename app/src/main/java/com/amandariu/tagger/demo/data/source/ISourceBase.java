package com.amandariu.tagger.demo.data.source;

import android.support.annotation.NonNull;

import com.amandariu.tagger.demo.data.Tag;

import java.util.List;

/**
 * @author amandariu (11/12/17)
 */
public interface ISourceBase {
    /**
     * Methods required for working with the TagsDataSource.
     */
    interface ILoadTagsCallback {
        /**
         * Tags have been loaded from a datasource.
         * @param tags The tags pulled from the datasource.
         */
        void onTagsLoaded(List<Tag> tags);

        /**
         * No tags available for loading.
         */
        void onDataNotAvailable(@NonNull String msg);
    }

    /**
     * Load tags from one or more datasources.
     * @param callback The callback to send the loaded tags to.
     */
    void getTags(@NonNull ILoadTagsCallback callback);

    /**
     * Gracefully shutdown and release references.
     */
    void shutdown();
}
