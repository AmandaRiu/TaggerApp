package com.amandariu.tagger.demo.data.source;

import android.support.annotation.NonNull;
import com.amandariu.tagger.ITag;
import java.util.List;

/**
 * Main entry point for accessing Tags data.
 */
public interface ITagsDataSource extends ISourceBase {

    interface ISaveTagsCallback {
        void onTagsSavedSuccess();
        void onTagsSavedError(Exception e);
    }

    void saveTags(@NonNull List<? extends ITag> tags, @NonNull ISaveTagsCallback callback);
}
