package com.amandariu.tagger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class TaggerActivity extends AppCompatActivity implements
        TagListFragment.TagListFragmentListener, TagChipsFragment.TagChipsFragmentListener {

    public static String ARG_AVAILABLE_TAGS = "ARG-AVAILABLE-TAGS";
    public static String ARG_SELECTED_TAGS = "ARG-SELECTED-TAGS";
    public static int RESULT_CODE = 1000;

    private TagChipsFragment mChipsFragment;
    private TagListFragment mListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagger);

        ArrayList<Tag> availableTags = getIntent().getParcelableArrayListExtra(ARG_AVAILABLE_TAGS);
        ArrayList<Tag> selectedTags = getIntent().getParcelableArrayListExtra(ARG_SELECTED_TAGS);

        if (savedInstanceState == null) {
            mChipsFragment = TagChipsFragment.newInstance(selectedTags);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_tagChips, mChipsFragment, "CHIPS")
                    .commit();

            mListFragment = TagListFragment.newInstance(availableTags);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_tagList, mListFragment, "LIST")
                    .commit();

        } else {
            mChipsFragment = (TagChipsFragment) getSupportFragmentManager().findFragmentByTag("CHIPS");
            mListFragment = (TagListFragment) getSupportFragmentManager().findFragmentByTag("LISTS");
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
        List<Tag> selectedTags = mChipsFragment.getSelectedTags();
        Intent data = new Intent();
        data.putParcelableArrayListExtra(ARG_SELECTED_TAGS, (ArrayList)selectedTags);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onTagSelectionChanged(Tag tag) {
        if (tag.isSelected()) {
            mChipsFragment.addTag(tag);
        } else {
            mChipsFragment.removeTag(tag);
        }
    }

    @Override
    public void onTagRemoved(Tag tag) {
        mListFragment.deselectTag(tag);
    }
}
