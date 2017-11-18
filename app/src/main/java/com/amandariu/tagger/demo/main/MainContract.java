package com.amandariu.tagger.demo.main;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amandariu.tagger.demo.IBasePresenter;
import com.amandariu.tagger.demo.IBaseView;
import com.amandariu.tagger.demo.data.Tag;

import java.util.List;

/**
 * Specifies the contract between the view and presenter for {@link MainActivity}
 *
 * @author Amanda Riu
 */
public interface MainContract {

    //region View
    interface View extends IBaseView<Presenter> {
        /**
         * Request the loading indicator be displayed or hidden.
         * @param active If true, show the loading indicator. If false, hide it.
         */
        void setLoadingIndicator(boolean active);

        /**
         * Update the view with the available tags provided.
         * @param tags The tags fetched from the Repo.
         */
        void setAvailableTags(@Nullable List<Tag> tags);

        /**
         * Update the view with the selected tags provided. These are the tags
         * the user selected using the custom Tag selector.
         * @param tags A list of tags the user selected.
         */
        void setSelectedTags(@Nullable List<Tag> tags);

        /**
         * Display an error message to the end user.
         * @param msg The message to be displayed.
         */
        void showError(@NonNull String msg);

        /**
         * @return True if the view is still active, else false.
         */
        boolean isActive();

        /**
         * Show the "no available tags for display" view.
         */
        void showNoAvailableTags();

        /**
         * Show the "no selected tags for display" view.
         */
        void showNoSelectedTags();
    }
    //endregion

    //region Presenter
    interface Presenter extends IBasePresenter {
        /**
         * Load the tags from the various data sources.
         * @param forceUpdate If true, rebuild the cache with fresh data.
         */
        void loadTags(boolean forceUpdate);

        /**
         * Explicitly load the tags from the local database.
         *
         */
        void loadTagsFromLocal();

        /**
         * Explicitly fetch tags from the remote api
         */
        void loadTagsFromRemote();

        /**
         * Open the custom Tag selector. Set pre-selected tags by sending in
         * a seed list.
         * @param seedSelected The list of tags that should already be selected.
         */
        void selectTags(@Nullable List<Tag> seedSelected);
    }
    //endregion
}
