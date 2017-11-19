package com.amandariu.tagger;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class TaggerActivity extends AppCompatActivity implements
        TagListFragment.TagListFragmentListener, TagChipsFragment.TagChipsFragmentListener {

    public static final String ARG_SELECTED_TAGS = "com.amandariu.tagger.SELECTED-TAGS";
    public static final String ARG_AVAILABLE_TAGS = "com.amandariu.tagger.AVAILABLE-TAGS";
    public static String ARG_TAG_EXTRAS = "com.amandariu.tagger.TAG-EXTRAS";

    public static int REQUEST_CODE = 1000;

    private TagChipsFragment mChipsFragment;
    private TagListFragment mListFragment;

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull List<? extends ITag> availTags,
                                      @Nullable List<? extends ITag> selectedTags) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagger);

        if (savedInstanceState == null) {
            if (getIntent().getBundleExtra(ARG_TAG_EXTRAS) == null) {
                throw new IllegalArgumentException("Tagger requires a list of available Tags to" +
                        " work properly. Please use TaggerActivity.createIntent(...) method to ensure" +
                        " all required data is provided.");
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
                    .replace(R.id.fragment_tagChips, mChipsFragment, mChipsFragment.TAG)
                    .commit();
            //
            // Available Tags List view
            mListFragment = TagListFragment.newInstance(availableTags, selectedTags);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_tagList, mListFragment, mListFragment.TAG)
                    .commit();

        } else {
            mChipsFragment = (TagChipsFragment) getSupportFragmentManager()
                    .findFragmentByTag(TagChipsFragment.TAG);
            mListFragment = (TagListFragment) getSupportFragmentManager()
                    .findFragmentByTag(TagListFragment.TAG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tagger_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close) {
            saveAndClose();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


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
        if (mChipsFragment != null) {
            mChipsFragment.removeTag(tag);
        }
    }

    //endregion
}
