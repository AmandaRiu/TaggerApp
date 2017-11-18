package com.amandariu.tagger.demo.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amandariu.tagger.demo.data.Tag;
import com.amandariu.tagger.demo.data.source.ISourceBase;
import com.amandariu.tagger.demo.data.source.ITagRepository;
import com.amandariu.tagger.demo.data.source.ITagsDataSource;
import com.amandariu.tagger.demo.data.source.TagsRepository;
import com.amandariu.tagger.demo.utils.EspressoIdlingResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Presenter for {@link MainActivity}. This presenter is responsible for interacting
 * with the Data layer and updating the view.
 *
 * @author amandariu (11/4/17)
 */
public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private ITagRepository mRepo;
    private MainContract.View mView;
    private ISourceBase.ILoadTagsCallback mGetAvailableTagsCallback;

    private boolean mFirstLoad = true;

    public MainPresenter(@NonNull TagsRepository repo, @NonNull MainContract.View view) {
        this.mRepo = checkNotNull(repo, "TagsRepository cannot be null!");
        this.mView = checkNotNull(view, "View cannot be null!");
        this.mView.setPresenter(this);
        mGetAvailableTagsCallback = new ITagsDataSource.ILoadTagsCallback() {

            @Override
            public void onTagsLoaded(List<Tag > tags) {
                //
                // This callback may be called twice, once for cache and once for loading
                // the data from the remote API, so we must check before decrementing.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();
                }
                //
                // Verify the view is still active
                if (!mView.isActive()) {
                    Log.d(TAG, "Tags have been loaded, but the view is no longer active.");
                    return;
                }
                //
                // Clear loading indicator
                mView.setLoadingIndicator(false);
                //
                // Display the appropriate view depending on the results
                if (tags.isEmpty()) {
                    //
                    // Show a message indicating their are no tags available.
                    mView.showNoAvailableTags();
                } else {
                    //
                    // Show the tags
                    mView.setAvailableTags(tags);
                }
            }

            @Override
            public void onDataNotAvailable(@NonNull String msg) {
                //
                // Verify the view is still active
                if (!mView.isActive()) {
                    return;
                }
                //
                // Clear loading indicator
                mView.setLoadingIndicator(false);
                //
                // Show message to user.
                mView.showError(msg);
            }
        };
    }

    /**
     * Initialize the presenter
     */
    @Override
    public void start() {
        loadTags(false);
    }

    /**
     * The view is being destroyed. Clear any references in the presenter.
     */
    @Override
    public void destroyView() {
        Log.v(TAG, "View is being destroyed. Shut down Repository and Presenter");
        if (mRepo != null) {
            mRepo.shutdown();
        }
        mRepo = null;
        mView = null;
    }

    /**
     * Load the tags from the various data sources.
     *
     * @param forceUpdate If true, rebuild the cache with fresh data.
     */
    @Override
    public void loadTags(boolean forceUpdate) {
        loadTags(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * Open the custom Tag selector. Set pre-selected tags by sending in
     * a seed list.
     *
     * @param seedSelected The list of tags that should already be selected.
     */
    @Override
    public void selectTags(@Nullable List<Tag> seedSelected) {

    }

    private void loadTags(boolean forceUpdate, final boolean showLoadingUi) {

        Log.d(TAG, "Loading tags with [forceUpdate = " + forceUpdate
                + ", showLoadingUi = " + showLoadingUi + "]");

        if (showLoadingUi) {
            mView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mRepo.refreshTags();
        }
        //
        // Testing Only:
        // The network request would be handled on a different thread so make sure
        // espresso knows the app is busy until the response is handled.
        EspressoIdlingResource.increment();

        mRepo.getTags(mGetAvailableTagsCallback);
    }


    /**
     * Explicitly load the tags from the local database.
     */
    @Override
    public void loadTagsFromLocal() {
        //
        // Testing Only:
        // The network request would be handled on a different thread so make sure
        // espresso knows the app is busy until the response is handled.
        EspressoIdlingResource.increment();

        mRepo.getTagsFromLocalDataSource(mGetAvailableTagsCallback);
    }

    /**
     * Explicitly fetch tags from the remote api
     */
    @Override
    public void loadTagsFromRemote() {
        //
        // Testing Only:
        // The network request would be handled on a different thread so make sure
        // espresso knows the app is busy until the response is handled.
        EspressoIdlingResource.increment();

        mRepo.getTagsFromRemoteDataSource(mGetAvailableTagsCallback);
    }
}
