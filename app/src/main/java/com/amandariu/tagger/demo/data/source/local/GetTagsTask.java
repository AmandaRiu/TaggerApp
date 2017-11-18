package com.amandariu.tagger.demo.data.source.local;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amandariu.tagger.demo.data.Tag;
import com.amandariu.tagger.demo.data.source.local.db.TagConverter;
import com.amandariu.tagger.demo.data.source.local.db.TagDatabase;
import com.amandariu.tagger.demo.data.source.local.db.TagEntity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.amandariu.tagger.demo.data.source.ISourceBase.ILoadTagsCallback;

/**
 * todo replace with a single threaded executor for accessing the database.
 * @author amandariu (11/12/17)
 */
public class GetTagsTask extends AsyncTask<Void, Void, List<TagEntity>> {

    private static final String TAG = GetTagsTask.class.getSimpleName();

    private ILoadTagsCallback mCallback;
    private TagDatabase mDb;

    public GetTagsTask(@NonNull TagDatabase db, @NonNull final ILoadTagsCallback callback) {
        this.mCallback = callback;
        this.mDb = db;
    }

    @Override
    protected List<TagEntity> doInBackground(Void... voids) {
        long startTime = System.nanoTime();
        List<TagEntity> tags = mDb.tagDao().loadAllTags();
        long elapsedTime = System.nanoTime() - startTime;
        Log.d(TAG, "Total time to get tags from database ["
                + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds");
        return tags;
    }

    @Override
    protected void onPostExecute(List<TagEntity> tags) {
        List<Tag> newTags = TagConverter.toTagList(tags);
        mCallback.onTagsLoaded(newTags);
    }
}