package com.amandariu.tagger.demo.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amandariu.tagger.ITag;
import com.amandariu.tagger.TaggerActivity;
import com.amandariu.tagger.demo.R;
import com.amandariu.tagger.demo.Injection;
import com.amandariu.tagger.demo.utils.AlertUtils;
import com.amandariu.tagger.demo.utils.AndroidUtils;
import com.amandariu.tagger.demo.utils.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainContract.Presenter mPresenter;
    //
    // View components
    private SimpleTagListAdapter mAvailableTagsAdapter;
    private SimpleTagListAdapter mSelectedTagsAdapter;
    private ViewGroup mViewLoading = null;
    private TextView mNoAvailableTags;
    private TextView mNoSelectedTags;
    //
    // State
    private List<? extends ITag> mAvailableTags = new ArrayList<>();
    private List<ITag> mSelectedTags = new ArrayList<>();

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
        // Display Available Tags
        mNoAvailableTags = findViewById(R.id.txt_noAvailableTags);
        RecyclerView availList = findViewById(R.id.list_availableTags);
        mAvailableTagsAdapter = new SimpleTagListAdapter(new ArrayList<ITag>());
        availList.setAdapter(mAvailableTagsAdapter);
        Button clearAvailableTags = findViewById(R.id.btn_clearAvailableTags);
        clearAvailableTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAvailableTags.clear();
                setAvailableTags(mAvailableTags);
            }
        });
        //
        // Display Selected Tags
        mNoSelectedTags = findViewById(R.id.txt_noSelectedTags);
        RecyclerView selectList = findViewById(R.id.list_selectedTags);
        mSelectedTagsAdapter = new SimpleTagListAdapter(new ArrayList<ITag>());
        selectList.setAdapter(mSelectedTagsAdapter);
        Button clearSelectedTags = findViewById(R.id.btn_clearSelectedTags);
        clearSelectedTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedTags.clear();
                setSelectedTags(mSelectedTags);
            }
        });
        //
        // Loading indicator
        mViewLoading = findViewById(R.id.view_loading);

        if (savedInstanceState != null) {
            //
            // Restore any selected tags saved during configuration change.
            Parcelable[] pSelTags = savedInstanceState
                    .getParcelableArray(TaggerActivity.ARG_SELECTED_TAGS);
            if (pSelTags != null && pSelTags.length > 0) {
                mSelectedTags.clear();
                for (Parcelable t : pSelTags) {
                    mSelectedTags.add((ITag)t);
                }
                setSelectedTags(mSelectedTags);
            }
            //
            // Restore available tags saved during configuration change
            Parcelable[] pAvaTags = savedInstanceState
                    .getParcelableArray(TaggerActivity.ARG_AVAILABLE_TAGS);
            if (pAvaTags != null && pAvaTags.length > 0) {
                List<ITag> tags = new ArrayList<>(pAvaTags.length);
                for (Parcelable t : pAvaTags) {
                    tags.add((ITag) t);
                }
                setAvailableTags(tags);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //
        // Save selected tags
        if (mSelectedTags.size() > 0) {
            outState.putParcelableArray(
                    TaggerActivity.ARG_SELECTED_TAGS,
                    mSelectedTags.toArray(new ITag[mSelectedTags.size()]));
        }
        //
        // Save available tags
        if (mAvailableTags != null && mAvailableTags.size() > 0) {
            outState.putParcelableArray(
                    TaggerActivity.ARG_AVAILABLE_TAGS,
                    mAvailableTags.toArray(new ITag[mAvailableTags.size()]));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.start();
    }


    @Override
    protected void onDestroy() {
        mPresenter.destroyView();
        mViewLoading = null;
        mSelectedTags = null;
        mAvailableTags = null;
        mAvailableTagsAdapter = null;
        mNoSelectedTags = null;
        mNoAvailableTags = null;
        mSelectedTagsAdapter = null;
        super.onDestroy();
    }


    //region MainContract.View
    /**
     * Set the presenter for the view.
     * @param presenter The presenter for this view.
     */
    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Request the loading indicator be displayed or hidden.
     * @param active If true, show the loading indicator. If false, hide it.
     */
    @Override
    public void setLoadingIndicator(boolean active) {
        mViewLoading.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    /**
     * Update the view with the available tags provided.
     * @param tags The tags fetched from the Repo.
     */
    @Override
    public void setAvailableTags(@Nullable List<? extends ITag> tags) {
        Log.d(TAG, "Updating view with available tags ["
                + (tags == null ? "null" : tags.size()) + "]");
        if (tags != null) {
            mAvailableTagsAdapter.setTags(tags);
        }
        if (tags != null && tags.size() > 0) {
            showNoAvailableTags(false);
        } else {
            showNoAvailableTags(true);
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
    public void setSelectedTags(@Nullable List<? extends ITag> tags) {
        Log.d(TAG, "Updating view with selected tags ["
                + (tags == null ? "null" : tags.size()) + "]");

        if (tags != null && !tags.isEmpty()) {
            showNoSelectedTags(false);
            mSelectedTagsAdapter.setTags(tags);
        } else {
            showNoSelectedTags(true);
        }
    }

    /**
     * Show the "no available tags for display" view.
     *
     * @param on True if the No Available Tags view should be displayed, else false.
     */
    @Override
    public void showNoAvailableTags(boolean on) {
        if (on) {
            mNoAvailableTags.setVisibility(View.VISIBLE);
        } else {
            mNoAvailableTags.setVisibility(View.GONE);
        }
    }

    /**
     * Show the "no selected tags for display" view.
     *
     * @param on True if the No Selected Tags view should be displayed, else false.
     */
    @Override
    public void showNoSelectedTags(boolean on) {
        if (on) {
            mNoSelectedTags.setVisibility(View.VISIBLE);
        } else {
            mNoSelectedTags.setVisibility(View.GONE);
        }
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
    //endregion


    //region ClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_api:
                showNoAvailableTags(true);
                mPresenter.loadTagsFromRemote();
                break;
            case R.id.btn_database:
                showNoAvailableTags(true);
                mPresenter.loadTagsFromLocal();
                break;
            case R.id.btn_selectTags:
                if (mAvailableTags != null && mAvailableTags.size() > 0) {
                    mPresenter.selectTags(this, mAvailableTags, mSelectedTags);
                } else {
                    Toast.makeText(
                            this,
                            R.string.no_tags_loaded,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.e(TAG, "Unknown view sent to onClick handler!");
                break;
        }
    }
    //endregion


    //region Testing
    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
    //endregion


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TaggerActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Parcelable[] pSelTags
                        = data.getParcelableArrayExtra(TaggerActivity.ARG_SELECTED_TAGS);
                mSelectedTags.clear();
                if (pSelTags != null && pSelTags.length > 0) {
                    for (Parcelable p : pSelTags) {
                        mSelectedTags.add((ITag)p);
                    }
                }
                setSelectedTags(mSelectedTags);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
