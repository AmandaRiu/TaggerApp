package com.amandariu.tagger.demo.main

import android.app.Activity
import android.content.Context

import com.amandariu.tagger.ITag
import com.amandariu.tagger.demo.IBasePresenter
import com.amandariu.tagger.demo.IBaseView

/**
 * Specifies the contract between the view and presenter for [MainActivity]
 *
 * @author Amanda Riu
 */
interface MainContract {

    //region View
    interface View : IBaseView<Presenter> {

        /**
         * @return True if the view is still active, else false.
         */
        val isActive: Boolean

        /**
         * Request the loading indicator be displayed or hidden.
         * @param active If true, show the loading indicator. If false, hide it.
         */
        fun setLoadingIndicator(active: Boolean)

        /**
         * Update the view with the available tags provided.
         * @param tags The tags fetched from the Repo.
         */
        fun setAvailableTags(tags: List<ITag>?)

        /**
         * Update the view with the selected tags provided. These are the tags
         * the user selected using the custom Tag selector.
         * @param tags A list of tags the user selected.
         */
        fun setSelectedTags(tags: List<ITag>?)

        /**
         * Display an error message to the end user.
         * @param msg The message to be displayed.
         */
        fun showError(msg: String)

        /**
         * Show the "no available tags for display" view.
         * @param on True if the No Available Tags view should be displayed, else false.
         */
        fun showNoAvailableTags(on: Boolean)

        /**
         * Show the "no selected tags for display" view.
         * @param on True if the No Selected Tags view should be displayed, else false.
         */
        fun showNoSelectedTags(on: Boolean)
    }
    //endregion

    //region Presenter
    interface Presenter : IBasePresenter {
        /**
         * Load the tags from the various data sources.
         * @param forceUpdate If true, rebuild the cache with fresh data.
         */
        fun loadTags(forceUpdate: Boolean)

        /**
         * Explicitly load the tags from the local database.
         */
        fun loadTagsFromLocal()

        /**
         * Explicitly fetch tags from the remote api.
         */
        fun loadTagsFromRemote(ctx: Context)

        /**
         * Open the custom Tag selector.
         *
         * @param activity The current activity.
         * @param availableTags A list of all available tags.
         * @param selectedTags A list of all selected tags.
         */
        fun selectTags(activity: Activity,
                       availableTags: List<ITag>,
                       selectedTags: List<ITag>?)
    }
    //endregion
}
