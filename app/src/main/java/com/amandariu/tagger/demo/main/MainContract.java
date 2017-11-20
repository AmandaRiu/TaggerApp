package com.amandariu.tagger.demo.main;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amandariu.tagger.ITag;
import com.amandariu.tagger.demo.IBasePresenter;
import com.amandariu.tagger.demo.IBaseView;

import java.util.List;

/**
 * Specifies the contract between the view and presenter for {@link MainActivity}
 *
 * @author Amanda Riu
 */
interface MainContract {

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
        void setAvailableTags(@Nullable List<? extends ITag> tags);

        /**
         * Update the view with the selected tags provided. These are the tags
         * the user selected using the custom Tag selector.
         * @param tags A list of tags the user selected.
         */
        void setSelectedTags(@Nullable List<? extends ITag> tags);

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
         * @param on True if the No Available Tags view should be displayed, else false.
         */
        void showNoAvailableTags(boolean on);

        /**
         * Show the "no selected tags for display" view.
         * @param on True if the No Selected Tags view should be displayed, else false.
         */
        void showNoSelectedTags(boolean on);
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
         */
        void loadTagsFromLocal();

        /**
         * Explicitly fetch tags from the remote api.
         */
        void loadTagsFromRemote(@NonNull Context ctx);

        /**
         * Open the custom Tag selector.
         *
         * @param activity The current activity.
         * @param availableTags A list of all available tags.
         * @param selectedTags A list of all selected tags.
         */
        void selectTags(@NonNull Activity activity,
                        @NonNull List<? extends ITag> availableTags,
                        @Nullable List<? extends ITag> selectedTags);
    }
    //endregion
}
