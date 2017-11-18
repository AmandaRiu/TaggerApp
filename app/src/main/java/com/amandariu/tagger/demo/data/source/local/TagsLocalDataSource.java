package com.amandariu.tagger.demo.data.source.local;

import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amandariu.tagger.demo.TaggerApplication;
import com.amandariu.tagger.demo.data.Tag;
import com.amandariu.tagger.demo.data.source.ITagsDataSource;
import com.amandariu.tagger.demo.data.source.local.db.TagConverter;
import com.amandariu.tagger.demo.data.source.local.db.TagDatabase;
import com.amandariu.tagger.demo.data.source.local.db.TagEntity;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author amandariu (11/11/17)
 */
public class TagsLocalDataSource implements ITagsDataSource {

    private static final String TAG = TagsLocalDataSource.class.getSimpleName();
    private TagDatabase mDb;


    public TagsLocalDataSource() {
        Log.v(TAG, "Initializing the local Tag Datasource");
        long startTime = System.nanoTime();
        mDb = Room.databaseBuilder(
                TaggerApplication.getInstance().getApplicationContext(),
                TagDatabase.class,
                TagDatabase.DATABASE_NAME)
                .build();
        long elapsedTime = System.nanoTime() - startTime;
        Log.d(TAG, "Total time to initialize database ["
                + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds");
    }

    /**
     * Load tags from one or more data sources.
     * @param callback The callback to send the loaded tags to.
     */
    @Override
    public void getTags(@NonNull ILoadTagsCallback callback) {
        Log.d(TAG, "Processing request to load tags from DB");
        new GetTagsTask(mDb, callback).execute();
    }

    /**
     * Save tags to the local database.
     * @param tags The list of tags to write to the database.
     * @param callback The callback to notify of result.
     */
    public void saveTags(@NonNull final List<Tag> tags,
                         @NonNull final ISaveTagsCallback callback) {
        Log.d(TAG, "Processing request to save ["
                + (tags == null ? "null" : tags.size()) + "] tags to the db");
        final List<TagEntity> tagEntities = TagConverter.toTagEntityList(tags);
        new SaveTagsTask(mDb, tagEntities, callback).execute();
    }

    /**
     * Shutdown the database.
     */
    @Override
    public void shutdown() {
        Log.v(TAG, "Shutting down the local tags datasource");
        if (mDb != null) {
            mDb.close();
        }
        mDb = null;
    }
}
