package com.amandariu.tagger.demo.main

import android.app.Activity
import android.content.Context
import android.util.Log

import com.amandariu.tagger.ITag
import com.amandariu.tagger.TaggerActivity
import com.amandariu.tagger.demo.R
import com.amandariu.tagger.demo.utils.isNetworkConnected
import com.amandariu.tagger.demo.data.source.ISourceBase
import com.amandariu.tagger.demo.data.source.ITagRepository
import com.amandariu.tagger.demo.data.source.TagsRepository
import com.amandariu.tagger.demo.utils.EspressoIdlingResource

import com.google.common.base.Preconditions.checkNotNull

/**
 * The Presenter for [MainActivity]. This presenter is responsible for interacting
 * with the Data layer and updating the view.
 *
 * @author Amanda Riu
 */
class MainPresenter internal constructor(repo: TagsRepository,
                                         view: MainContract.View) : MainContract.Presenter {

    companion object {
        private val TAG = MainPresenter::class.java.simpleName
    }

    private var mRepo: ITagRepository? = null
    private var mView: MainContract.View? = null
    private val mGetAvailableTagsCallback: ISourceBase.ILoadTagsCallback

    private var mFirstLoad = true

    init {
        this.mRepo = checkNotNull(repo, "TagsRepository cannot be null!")
        this.mView = checkNotNull(view, "View cannot be null!")
        this.mView!!.setPresenter(this)
        mGetAvailableTagsCallback = object : ISourceBase.ILoadTagsCallback {

            override fun onTagsLoaded(tags: List<ITag>) {
                //
                // This callback may be called twice, once for cache and once for loading
                // the data from the remote API, so we must check before decrementing.
                if (!EspressoIdlingResource.idlingResource.isIdleNow) {
                    EspressoIdlingResource.decrement()
                }
                //
                // Verify the view is still active
                if (!mView!!.isActive) {
                    Log.d(TAG, "Tags have been loaded, but the view is no longer active.")
                    return
                }
                //
                // Clear loading indicator
                mView!!.setLoadingIndicator(false)
                mView!!.setAvailableTags(tags)
            }

            override fun onDataNotAvailable(msg: String) {
                //
                // Verify the view is still active
                if (!mView!!.isActive) {
                    return
                }
                //
                // Clear loading indicator
                mView!!.setLoadingIndicator(false)
                //
                // Show message to user.
                mView!!.showError(msg)
            }
        }
    }

    /**
     * Initialize the presenter
     */
    override fun start() {
        loadTags(false)
    }

    /**
     * The view is being destroyed. Clear any references in the presenter.
     */
    override fun destroyView() {
        Log.v(TAG, "View is being destroyed. Shut down Repository and Presenter")
        if (mRepo != null) {
            mRepo!!.shutdown()
        }
        mRepo = null
        mView = null
    }

    /**
     * Load the tags from the various data sources.
     * @param forceUpdate If true, rebuild the cache with fresh data.
     */
    override fun loadTags(forceUpdate: Boolean) {
        loadTags(forceUpdate || mFirstLoad, true)
        mFirstLoad = false
    }

    /**
     * Open the custom Tag selector.
     *
     * @param [activity] The current activity.
     * @param [availableTags] A list of all available tags.
     * @param [selectedTags] A list of all selected tags.
     */
    override fun selectTags(activity: Activity,
                            availableTags: List<ITag>,
                            selectedTags: List<ITag>?) {

        val intent = TaggerActivity.createIntent(activity, availableTags, selectedTags)
        activity.startActivityForResult(intent, TaggerActivity.REQUEST_CODE)
    }

    private fun loadTags(forceUpdate: Boolean, showLoadingUi: Boolean) {

        Log.d(TAG, "Loading tags with [forceUpdate = " + forceUpdate
                + ", showLoadingUi = " + showLoadingUi + "]")

        if (showLoadingUi) {
            mView!!.setLoadingIndicator(true)
        }
        if (forceUpdate) {
            mRepo!!.refreshTags()
        }
        //
        // Testing Only:
        // The network request would be handled on a different thread so make sure
        // espresso knows the app is busy until the response is handled.
        EspressoIdlingResource.increment()

        mRepo!!.getTags(mGetAvailableTagsCallback)
    }

    /**
     * Explicitly load the tags from the local database.
     */
    override fun loadTagsFromLocal() {
        //
        // Testing Only:
        // The network request would be handled on a different thread so make sure
        // espresso knows the app is busy until the response is handled.
        EspressoIdlingResource.increment()

        mRepo!!.getTagsFromLocalDataSource(mGetAvailableTagsCallback)
    }

    /**
     * Explicitly fetch tags from the remote api
     */
    override fun loadTagsFromRemote(ctx: Context) {
        if (!isNetworkConnected(ctx)) {
            mView!!.showError(ctx.getString(R.string.error_no_internet))
        }
        //
        // Testing Only:
        // The network request would be handled on a different thread so make sure
        // espresso knows the app is busy until the response is handled.
        EspressoIdlingResource.increment()

        mRepo!!.getTagsFromRemoteDataSource(mGetAvailableTagsCallback)
    }
}
