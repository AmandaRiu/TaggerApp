package com.amandariu.tagger.demo.data.source.local;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amandariu.tagger.demo.data.source.local.db.TagDatabase;
import com.amandariu.tagger.demo.data.source.local.db.TagEntity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.amandariu.tagger.demo.data.source.ITagsDataSource.ISaveTagsCallback;

/**
 * Save tags to the database for offline use.
 *
 * @author Amanda Riu
 */
public class SaveTagsTask extends AsyncTask<Void, Void, Void> {

    private final String TAG = SaveTagsTask.class.getSimpleName();

    private final WeakReference<ISaveTagsCallback> mCallback;
    private final TagDatabase mDb;
    private final List<TagEntity> mTags;

    public SaveTagsTask(@NonNull TagDatabase db,
                        @NonNull List<TagEntity> tags,
                        @NonNull ISaveTagsCallback callback) {
        mCallback = new WeakReference<>(callback);
        mDb = db;
        mTags = tags;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        long startTime = System.nanoTime();
        try {
            mDb.tagDao().insertAll(mTags);
            long elapsedTime = System.nanoTime() - startTime;
            Log.d(TAG, "Total time to save Tags to database ["
                    + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + "] seconds");
            ISaveTagsCallback callback = mCallback.get();
            if (callback != null) {
                callback.onTagsSavedSuccess();
            }
        } catch (Exception e) {
            ISaveTagsCallback callback = mCallback.get();
            if (callback != null) {
                callback.onTagsSavedError(e);
            }
        }
        return null;
    }
}
