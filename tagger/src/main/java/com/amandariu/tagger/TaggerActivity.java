package com.amandariu.tagger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity for displaying two views:
 * <ul>
 *     <li>Selected Tags - displays each selected tag as a {@link TagChipView}</li>
 *     <li>Available Tags - A list of all available tags.</li>
 * </ul>
 * There are two layout configurations for this Activity:
 * <ul>
 *     <li>portrait - Stacks the Selected and Available Tags Views on top of each other.</li>
 *     <li>landscape - Displays the Selected and Available Tags Views side-by-side.</li>
 * </ul>
 *
 * To ensure this class is properly instantiated, use {@link #createIntent(Context, List, List)}.
 * Example:
 * <code>
 *     Intent intent = TaggerActivity.createIntent(this, mAvailableTags, mSelectedTags);
 *     startActivityForResult(intent, TaggerActivity.REQUEST_CODE);
 * </code>
 * @author Amanda Riu
 */
public class TaggerActivity extends AppCompatActivity implements
        TagListFragment.TagListFragmentListener,
        TagChipsFragment.TagChipsFragmentListener,
        SearchView.OnQueryTextListener {

    public static final String ARG_SELECTED_TAGS = "com.amandariu.tagger.SELECTED-TAGS";
    public static final String ARG_AVAILABLE_TAGS = "com.amandariu.tagger.AVAILABLE-TAGS";
    public static String ARG_TAG_EXTRAS = "com.amandariu.tagger.TAG-EXTRAS";
    private static final String ARG_SEARCH_QUERY_STRING = "com.amandariu.tagger.SEARCH-QUERY-STRING";

    public static int REQUEST_CODE = 1000;

    private TagChipsFragment mChipsFragment;
    private TagListFragment mListFragment;
    private boolean mHasChanges = false;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private String mSearchQuery = null;
    private Handler mMainHandler = null;

    /**
     * Creates an intent for launching this activity. Using this method ensures the
     * intent is properly initialized with the available and selected tags.
     *
     * @param context The context of the calling Activity.
     * @param availTags The list of available {@link ITag}s. Cannot be null.
     * @param selectedTags The list of selected {@link ITag}s. Can be null.
     * @return The intent to use for launching this activity.
     */
    public static Intent createIntent(@NonNull Context context,
                                      @NonNull List<? extends ITag> availTags,
                                      @Nullable List<? extends ITag> selectedTags) {
        if (availTags == null) {
            throw new IllegalArgumentException("Available tags must not be null!");
        }
        Intent intent = new Intent(context, TaggerActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelableArray(
                ARG_AVAILABLE_TAGS, availTags.toArray(new ITag[availTags.size()]));
        if (selectedTags != null) {
            extras.putParcelableArray(
                    ARG_SELECTED_TAGS, selectedTags.toArray(new ITag[selectedTags.size()]));
        }
        intent.putExtra(ARG_TAG_EXTRAS, extras);
        return intent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagger);

        mMainHandler = new Handler();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            if (getIntent().getBundleExtra(ARG_TAG_EXTRAS) == null) {
                throw new IllegalArgumentException("Tagger requires a list of available Tags to" +
                        " work properly. Please use TaggerActivity.createIntent(...) method to" +
                        " ensure all required data is provided.");
            }
            Bundle extras = getIntent().getBundleExtra(ARG_TAG_EXTRAS);
            //
            // Populate Selected Tags
            List<ITag> selectedTags;
            Parcelable[] pSelTags = extras.getParcelableArray(ARG_SELECTED_TAGS);
            selectedTags = new ArrayList<>();
            if (pSelTags != null) {
                for (Parcelable p : pSelTags) {
                    selectedTags.add((ITag)p);
                }
            }
            //
            // Populate Available Tags
            Parcelable[] pAvaTags = extras.getParcelableArray(ARG_AVAILABLE_TAGS);
            if (pAvaTags == null || pAvaTags.length == 0) {
                throw new IllegalArgumentException("Tagger requires a list of available Tags to" +
                        " work properly. Please use TaggerActivity.createIntent(...) method to ensure" +
                        " all required data is provided.");
            }
            List<ITag> availableTags = new ArrayList<>();
            for (Parcelable p : pAvaTags) {
                if (!(p instanceof ITag)) {
                    throw new ClassCastException("Invalid Array of Available Tags. " +
                            "The tags MUST extend ITag!");
                }
                availableTags.add((ITag)p);
            }
            //
            // Selected Tag Chips view
            mChipsFragment = TagChipsFragment.newInstance(selectedTags);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_tagChips, mChipsFragment, TagChipsFragment.TAG)
                    .commit();
            //
            // Available Tags List view
            mListFragment = TagListFragment.newInstance(availableTags, selectedTags);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_tagList, mListFragment, TagListFragment.TAG)
                    .commit();

        } else {
            //
            // Restore reference to fragments.
            mChipsFragment = (TagChipsFragment) getSupportFragmentManager()
                    .findFragmentByTag(TagChipsFragment.TAG);
            mListFragment = (TagListFragment) getSupportFragmentManager()
                    .findFragmentByTag(TagListFragment.TAG);
            //
            // Grab the saved query for the search bar if available.
            String query = savedInstanceState.getString(ARG_SEARCH_QUERY_STRING, null);
            if (query != null) {
                mSearchQuery = query;
            }
        }
    }

    @Override
    protected void onDestroy() {
        //
        // Release references
        mChipsFragment = null;
        mListFragment = null;
        mSearchView = null;
        mSearchQuery = null;
        mSearchMenuItem = null;
        mMainHandler = null;
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tagger_menu, menu);

        mSearchMenuItem  = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //
        // Restore the state of the search bar if needed.
        if (mSearchQuery != null && !mSearchQuery.isEmpty()) {
            final String query = mSearchQuery;
            if (mMainHandler != null && mSearchMenuItem != null && mSearchView != null) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchMenuItem.expandActionView();
                        mSearchView.setQuery(query, true);
                        mSearchView.clearFocus();
                    }
                });
            }
            mSearchQuery = null;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            saveAndClose();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            verifyCancel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //
        // Save the state of the search view if we're to restore.
        if (mSearchView.isShown()) {
            outState.putString(ARG_SEARCH_QUERY_STRING, mSearchView.getQuery().toString());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * If the user has pending changes, then display a confirmation dialog advising the user
     * that all changes will be lost. If no changes, just cancel and return to the
     * calling activity.
     */
    private void verifyCancel() {
        if (mHasChanges) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tagger_confirm)
                    .setMessage(R.string.tagger_confirm_cancel_msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            cancelAndClose();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create().show();
        } else {
            cancelAndClose();
        }
    }

    /**
     * Cancels this activity without making any changes.
     */
    private void cancelAndClose() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Grab a list of selected tags and return to the calling Activity.
     */
    private void saveAndClose() {
        List<ITag> selectedTags = mChipsFragment.getSelectedTags();
        Intent data = new Intent();
        data.putExtra(ARG_SELECTED_TAGS, selectedTags.toArray(new ITag[selectedTags.size()]));
        setResult(RESULT_OK, data);
        finish();
    }

    //region TagChipsFragmentListener
    /**
     * User removed the Tag from the Tag Chips View by closing it.
     * @param tag The {@link ITag} that has been removed from the
     *            selected list.
     */
    @Override
    public void onTagChipClosed(ITag tag) {
        mHasChanges = true;
        if (mListFragment != null) {
            mListFragment.deselectTag(tag);
        }
    }
    //endregion

    //region TagListFragmentListener

    /**
     * Notifies listeners a Tag has been selected.
     * @param tag The newly selected {@link ITag}.
     */
    @Override
    public void onTagSelected(ITag tag) {
        mHasChanges = true;
        if (mChipsFragment != null) {
            mChipsFragment.addTag(tag);
        }
    }

    /**
     * Notifies listeners a Tag has been de-selected.
     * @param tag The newly de-selected {@link ITag}.
     */
    @Override
    public void onTagDeselected(ITag tag) {
        mHasChanges = true;
        if (mChipsFragment != null) {
            mChipsFragment.removeTag(tag);
        }
    }
    //endregion

    //region Search
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (mChipsFragment != null) {
            mChipsFragment.onQueryTextChange(newText);
        }
        if (mListFragment != null) {
            mListFragment.onQueryTextChange(newText);
        }
        return true;
    }
    //endregion
}
