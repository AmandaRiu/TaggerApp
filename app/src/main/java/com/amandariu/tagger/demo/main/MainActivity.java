package com.amandariu.tagger.demo.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amandariu.tagger.ITag;
import com.amandariu.tagger.TaggerActivity;
import com.amandariu.tagger.demo.R;
import com.amandariu.tagger.demo.Injection;
import com.amandariu.tagger.demo.data.Tag;
import com.amandariu.tagger.demo.utils.AlertUtils;
import com.amandariu.tagger.demo.utils.AndroidUtils;
import com.amandariu.tagger.demo.utils.EspressoIdlingResource;
import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainContract.Presenter mPresenter;
    //
    // View components
    private TextView mTxtAvailableTags = null;
    private TextView mTxtSelectedTags = null;
    private ViewGroup mViewLoading = null;
    //
    // State
    private List<Tag> mAvailableTags = null;
    private List<Tag> mSelectedTags = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        // Create the presenter
        new MainPresenter(Injection.provideTagsRepository(getApplicationContext()), this);
        //
        // Initialize View Components
        //
        // Get Tags from API
        Button btnGetApi = findViewById(R.id.btn_api);
        btnGetApi.setOnClickListener(this);
        //
        // Get Tags from Database
        Button btnGetDb = findViewById(R.id.btn_database);
        btnGetDb.setOnClickListener(this);
        //
        // Open the Tag Selector
        Button btnSelectTags = findViewById(R.id.btn_selectTags);
        btnSelectTags.setOnClickListener(this);
        //
        // Display Available and Selected Tags
        mTxtAvailableTags = findViewById(R.id.txt_availableTags);
        mTxtSelectedTags = findViewById(R.id.txt_selectedTags);
        //
        // Loading indicator
        mViewLoading = findViewById(R.id.view_loading);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }


    @Override
    protected void onDestroy() {
        mPresenter.destroyView();
        super.onDestroy();
    }


    //region MainContract.View
    /**
     * Set the presenter for the view.
     *
     * @param presenter The presenter for this view.
     */
    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Request the loading indicator be displayed or hidden.
     *
     * @param active If true, show the loading indicator. If false, hide it.
     */
    @Override
    public void setLoadingIndicator(boolean active) {
        mViewLoading.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    /**
     * Update the view with the available tags provided.
     *
     * @param tags The tags fetched from the Repo.
     */
    @Override
    public void setAvailableTags(@Nullable List<Tag> tags) {
        Log.d(TAG, "Updating view with available tags ["
                + (tags == null ? "null" : tags.size()) + "]");

        if (tags != null && !tags.isEmpty()) {
            try {
                String json = LoganSquare.serialize(tags);
                mTxtAvailableTags.setText(json);
            } catch (IOException e) {
                Log.e(TAG, "Error parsing available tags JSON!", e);
            }
        } else {
            mTxtSelectedTags.setText(R.string.no_avail_tags);
        }
        mAvailableTags = tags;
    }

    /**
     * Update the view with the selected tags provided. These are the tags
     * the user selected using the custom Tag selector.
     *
     * @param tags A list of tags the user selected.
     */
    @Override
    public void setSelectedTags(@Nullable List<Tag> tags) {
        Log.d(TAG, "Updating view with selected tags ["
                + (tags == null ? "null" : tags.size()) + "]");

        if (tags != null && !tags.isEmpty()) {
            try {
                String json = LoganSquare.serialize(tags);
                mTxtSelectedTags.setText(json);
            } catch (IOException e) {
                Log.e(TAG, "Error parsing JSON!", e);
            }
        } else {
            mTxtSelectedTags.setText(R.string.no_sel_tags);
        }
        mSelectedTags = tags;
    }

    /**
     * Display an error message to the end user.
     *
     * @param msg The message to be displayed.
     */
    @Override
    public void showError(@NonNull String msg) {
        AlertUtils.displayErrorMessage(this, msg);
    }

    /**
     * @return True if the view is still active, else false.
     */
    @Override
    public boolean isActive() {
        return !AndroidUtils.hasMinimumApi(17) || !isDestroyed();
    }

    /**
     * Show the "no available tags for display" view.
     */
    @Override
    public void showNoAvailableTags() {
        // TODO: 11/5/17 needed?
    }

    /**
     * Show the "no selected tags for display" view.
     */
    @Override
    public void showNoSelectedTags() {
        // TODO: 11/5/17 needed?
    }
    //endregion


    //region ClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_api:
                mPresenter.loadTagsFromRemote();
                break;
            case R.id.btn_database:
                mPresenter.loadTagsFromLocal();
                break;

            case R.id.btn_selectTags:
                openTagSelector();
                break;

            default:
                Log.e(TAG, "Unknown view sent to onClick handler!");
                break;
        }
    }
    //endregion


    private void openTagSelector() {
        Intent intent = new Intent(MainActivity.this, TaggerActivity.class);
        List<com.amandariu.tagger.Tag> taggerAvailableTags = new ArrayList<>();
        List<com.amandariu.tagger.Tag> taggerSelectedTags = new ArrayList<>();
        if (mAvailableTags == null || mAvailableTags.size() == 0) {
            showError(getString(R.string.error_no_available_tags));
        }
        for (Tag t : mAvailableTags) {
            taggerAvailableTags.add(new com.amandariu.tagger.Tag(t));
        }
        if (mSelectedTags != null && mSelectedTags.size() > 0) {
            for (Tag t : mSelectedTags) {
                taggerSelectedTags.add(new com.amandariu.tagger.Tag(t));
            }
        }
        intent.putParcelableArrayListExtra(TaggerActivity.ARG_AVAILABLE_TAGS, (ArrayList)taggerAvailableTags);
        intent.putParcelableArrayListExtra(TaggerActivity.ARG_SELECTED_TAGS, (ArrayList)taggerSelectedTags);
        startActivityForResult(intent, TaggerActivity.RESULT_CODE);
    }

    //region Testing
    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
    //endregion


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TaggerActivity.RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<com.amandariu.tagger.Tag> tags = data.getParcelableArrayListExtra(TaggerActivity.ARG_SELECTED_TAGS);
                List<Tag> tagsList = new ArrayList<>();
                for (com.amandariu.tagger.Tag t : tags) {
                    tagsList.add(new Tag((ITag)t));
                }
                setSelectedTags(tagsList);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
