package com.amandariu.tagger.demo.data.source;

import android.support.annotation.NonNull;
import android.util.Log;

import com.amandariu.tagger.ITag;
import com.amandariu.tagger.demo.common.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Centralized gateway for working with remote and local repositories.
 *
 * @author Amanda Riu
 */
public class TagsRepository implements ITagRepository {

    private static final String TAG = TagsRepository.class.getSimpleName();

    private static TagsRepository sInstance = null;
    private ITagsDataSource mTagsRemoteDataSource;
    private ITagsDataSource mTagsLocalDataSource;
    private boolean mIsFetchingTags = false;


    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    List<ITag> mCachedTags;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This
     * variable has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    /**
     * Singleton class. Prevent remote instantiation.
     *
     * @param remoteDataSource The datasource for connecting to the remote tags API.
     */
    private TagsRepository(@NonNull ITagsDataSource remoteDataSource,
                           @NonNull ITagsDataSource localDatasource) {
        mTagsRemoteDataSource = remoteDataSource;
        mTagsLocalDataSource = localDatasource;
    }

    /**
     * Returns the single instance of this Singleton, creating it if necessary.
     *
     * @param remoteDatasource The remote data source.
     * @return The {@link TagsRepository} instance.
     */
    public static TagsRepository getInstance(@NonNull ITagsDataSource remoteDatasource,
                                             @NonNull ITagsDataSource localDatasource) {
        if (sInstance == null) {
            sInstance = new TagsRepository(remoteDatasource, localDatasource);
        }
        return sInstance;
    }


    /**
     * Shut down this repository and all attached datasources.
     */
    @Override
    public void shutdown() {
        Log.v(TAG, "Shutting down TagRepository");

        if (mTagsLocalDataSource != null) {
            mTagsLocalDataSource.shutdown();
            mTagsLocalDataSource = null;
        }
        if (mTagsRemoteDataSource != null) {
            mTagsRemoteDataSource.shutdown();
            mTagsRemoteDataSource = null;
        }
        sInstance = null;
    }


    /**
     * Load tags from cache, local or remote data sources, which ever is available first.
     * <p>
     *     Note: {@link ILoadTagsCallback#onDataNotAvailable(String)} is fired if all data sources
     *     fail to get the data.
     * </p>
     *
     * @param callback The callback to send the loaded tags to.
     */
    @Override
    public void getTags(@NonNull ILoadTagsCallback callback) {

        if (mIsFetchingTags) {
            Log.w(TAG, "TagsRepository is already fetching tags...do nothing.");
            return; // already actively pulling tags.
        }

        checkNotNull(callback);
        if (!mCacheIsDirty && (mCachedTags != null && mCachedTags.size() > 0)) {
            //
            // Return cached tags
            Log.d(TAG, "Returning tags from cache");
            mIsFetchingTags = false;
            callback.onTagsLoaded(mCachedTags);
        } else {
            //
            // If network connected, pull the tags from the remote api to 
            // refresh the database. 
            if (NetworkUtils.isNetworkConnected()) {
                //
                // Grab tags from remote api.
                Log.d(TAG, "Fetching tags from the remote API");
                getTagsFromRemoteDataSource(callback);
            } else {
                //
                // Load tags from the database
                Log.d(TAG, "Device not connected, loading tags from database");
                getTagsFromLocalDataSource(callback);
            }
        }
    }


    /**
     * Set the dirty bit to true so the cached tags will be refreshed next time
     * they are requested.
     */
    public void refreshTags() {
        mCacheIsDirty = true;
    }


    /**
     * Fetch tags from the remote api data source. If tags are successfully fetched, the
     * cache will be updated, as well as the local data source. The provided callback will be
     * notified of the results.
     *
     * @param callback The callback to send the results.
     */
    @Override
    public void getTagsFromRemoteDataSource(@NonNull final ILoadTagsCallback callback) {
        mTagsRemoteDataSource.getTags(new ILoadTagsCallback() {
            @Override
            public void onTagsLoaded(List<? extends ITag> tags) {
                refreshCache(tags);
                refreshLocalDataSource(tags);
                callback.onTagsLoaded(tags);
                mIsFetchingTags = false;
            }

            @Override
            public void onDataNotAvailable(@NonNull String msg) {
                callback.onDataNotAvailable(msg);
                mIsFetchingTags = false;
            }
        });
    }

    /**
     * Fetch tags from the local datasource. If tags are successfully loaded, the cache will
     * be updated. The provided callback will be notified of the results.
     * @param callback The callback to send the results.
     */
    @Override
    public void getTagsFromLocalDataSource(@NonNull final ILoadTagsCallback callback) {
        mTagsLocalDataSource.getTags(new ILoadTagsCallback() {
            @Override
            public void onTagsLoaded(List<? extends ITag> tags) {
                refreshCache(tags);
                callback.onTagsLoaded(tags);
                mIsFetchingTags = false;
            }

            @Override
            public void onDataNotAvailable(@NonNull String msg) {
                callback.onDataNotAvailable(msg);
                mIsFetchingTags = false;
            }
        });
    }


    /**
     * Updates the local cache with a fresh copy of all the tags.
     *
     * @param tags The tags to save to cache.
     */
    private void refreshCache(List<? extends ITag> tags) {
        if (mCachedTags == null) {
            mCachedTags = new ArrayList<>();
        }
        mCachedTags.clear();
        for (ITag tag : tags) {
            mCachedTags.add(tag);
        }
        mCacheIsDirty = false;
    }


    /**
     * Save the provided tags to the local data source.
     *
     * @param tags The tags to be saved to the local data source.
     */
    private void refreshLocalDataSource(List<? extends ITag> tags) {
        if (mTagsLocalDataSource != null) {
            mTagsLocalDataSource.saveTags(tags, new ITagsDataSource.ISaveTagsCallback() {
                @Override
                public void onTagsSavedSuccess() {
                    Log.d(TAG, "Tags successfully saved to the local database");
                }

                @Override
                public void onTagsSavedError(Exception e) {
                    Log.e(TAG, "Error saving tags to the local database", e);
                }
            });
        }
    }
}
