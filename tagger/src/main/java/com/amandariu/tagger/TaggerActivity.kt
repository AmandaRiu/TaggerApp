package com.amandariu.tagger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem

import java.util.ArrayList

/**
 * Main Activity for displaying two views:
 *
 *  * Selected Tags - displays each selected tag as a [TagChipView]
 *  * Available Tags - A list of all available tags.
 *
 * There are two layout configurations for this Activity:
 *
 *  * portrait - Stacks the Selected and Available Tags Views on top of each other.
 *  * landscape - Displays the Selected and Available Tags Views side-by-side.
 *
 *
 * To ensure this class is properly instantiated, use [.createIntent].
 * Example:
 * `
 * Intent intent = TaggerActivity.createIntent(this, mAvailableTags, mSelectedTags);
 * startActivityForResult(intent, TaggerActivity.REQUEST_CODE);
` *
 * @author Amanda Riu
 */
class TaggerActivity : AppCompatActivity(), TagListFragment.TagListFragmentListener,
        TagChipsFragment.TagChipsFragmentListener, SearchView.OnQueryTextListener {

    companion object {
        @JvmField val ARG_SELECTED_TAGS = "com.amandariu.tagger.SELECTED-TAGS"
        @JvmField val ARG_AVAILABLE_TAGS = "com.amandariu.tagger.AVAILABLE-TAGS"
        @JvmField val ARG_TAG_EXTRAS = "com.amandariu.tagger.TAG-EXTRAS"
        private val ARG_SEARCH_QUERY_STRING = "com.amandariu.tagger.SEARCH-QUERY-STRING"

        @JvmField val REQUEST_CODE = 1000

        /**
         * Creates an intent for launching this activity. Using this method ensures the
         * intent is properly initialized with the available and selected tags.
         *
         * @param context The context of the calling Activity.
         * @param availTags The list of available [ITag]s. Cannot be null.
         * @param selectedTags The list of selected [ITag]s. Can be null.
         * @return The intent to use for launching this activity.
         */
        @JvmStatic
        fun createIntent(@NonNull context: Context,
                         @NonNull availTags: List<ITag>,
                         selectedTags: List<ITag>?): Intent {

            val intent = Intent(context, TaggerActivity::class.java)
            val extras = Bundle()
            extras.putParcelableArray(
                    ARG_AVAILABLE_TAGS, availTags.toTypedArray())
            if (selectedTags != null) {
                extras.putParcelableArray(
                        ARG_SELECTED_TAGS, selectedTags.toTypedArray())
            }
            intent.putExtra(ARG_TAG_EXTRAS, extras)
            return intent
        }
    }

    private var mChipsFragment: TagChipsFragment? = null
    private var mListFragment: TagListFragment? = null
    private var mHasChanges = false
    private var mSearchView: SearchView? = null
    private var mSearchMenuItem: MenuItem? = null
    private var mSearchQuery: String? = null
    private var mMainHandler: Handler? = null

    /**
     * {@inheritDoc}
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tagger)

        mMainHandler = Handler()

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            if (intent.getBundleExtra(ARG_TAG_EXTRAS) == null) {
                throw IllegalArgumentException("Tagger requires a list of available Tags to" +
                        " work properly. Please use TaggerActivity.createIntent(...) method to" +
                        " ensure all required data is provided.")
            }
            val extras = intent.getBundleExtra(ARG_TAG_EXTRAS)
            //
            // Populate Selected Tags
            val selectedTags: MutableList<ITag>
            val pSelTags = extras.getParcelableArray(ARG_SELECTED_TAGS)
            selectedTags = ArrayList()
            if (pSelTags != null) {
                for (p in pSelTags) {
                    selectedTags.add(p as ITag)
                }
            }
            //
            // Populate Available Tags
            val pAvaTags = extras.getParcelableArray(ARG_AVAILABLE_TAGS)
            if (pAvaTags == null || pAvaTags.size == 0) {
                throw IllegalArgumentException("Tagger requires a list of available Tags to" +
                        " work properly. Please use TaggerActivity.createIntent(...) method to ensure" +
                        " all required data is provided.")
            }
            val availableTags = ArrayList<ITag>()
            for (p in pAvaTags) {
                if (p !is ITag) {
                    throw ClassCastException("Invalid Array of Available Tags. "
                            + "The tags MUST extend ITag!")
                }
                availableTags.add(p)
            }
            //
            // Selected Tag Chips view
            mChipsFragment = TagChipsFragment.newInstance(selectedTags)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_tagChips, mChipsFragment, TagChipsFragment.TAG)
                    .commit()
            //
            // Available Tags List view
            mListFragment = TagListFragment.newInstance(availableTags, selectedTags)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_tagList, mListFragment, TagListFragment.TAG)
                    .commit()

        } else {
            //
            // Restore reference to fragments.
            mChipsFragment = supportFragmentManager
                    .findFragmentByTag(TagChipsFragment.TAG) as TagChipsFragment
            mListFragment = supportFragmentManager
                    .findFragmentByTag(TagListFragment.TAG) as TagListFragment
            //
            // Grab the saved query for the search bar if available.
            val query = savedInstanceState.getString(ARG_SEARCH_QUERY_STRING, null)
            if (query != null) {
                mSearchQuery = query
            }
        }
    }

    override fun onDestroy() {
        //
        // Release references
        mChipsFragment = null
        mListFragment = null
        mSearchView = null
        mSearchQuery = null
        mSearchMenuItem = null
        mMainHandler = null
        super.onDestroy()
    }

    /**
     * {@inheritDoc}
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tagger_menu, menu)

        mSearchMenuItem = menu.findItem(R.id.action_search)
        mSearchView = mSearchMenuItem!!.actionView as SearchView
        mSearchView!!.setOnQueryTextListener(this)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        //
        // Restore the state of the search bar if needed.
        if (mSearchQuery != null && !mSearchQuery!!.isEmpty()) {
            val query = mSearchQuery
            if (mMainHandler != null && mSearchMenuItem != null && mSearchView != null) {
                mMainHandler!!.post {
                    mSearchMenuItem!!.expandActionView()
                    mSearchView!!.setQuery(query, true)
                    mSearchView!!.clearFocus()
                }
            }
            mSearchQuery = null
        }
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * {@inheritDoc}
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_close) {
            saveAndClose()
            return true
        } else if (item.itemId == android.R.id.home) {
            verifyCancel()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        //
        // Save the state of the search view if we're to restore.
        if (mSearchView!!.isShown) {
            outState!!.putString(ARG_SEARCH_QUERY_STRING, mSearchView!!.query.toString())
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * If the user has pending changes, then display a confirmation dialog advising the user
     * that all changes will be lost. If no changes, just cancel and return to the
     * calling activity.
     */
    private fun verifyCancel() {
        if (mHasChanges) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.tagger_confirm)
                    .setMessage(R.string.tagger_confirm_cancel_msg)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        dialog.cancel()
                        cancelAndClose()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                    .create().show()
        } else {
            cancelAndClose()
        }
    }

    /**
     * Cancels this activity without making any changes.
     */
    private fun cancelAndClose() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /**
     * Grab a list of selected tags and return to the calling Activity.
     */
    private fun saveAndClose() {
        val selectedTags = mChipsFragment!!.selectedTags
        val data = Intent()
        data.putExtra(ARG_SELECTED_TAGS, selectedTags.toTypedArray())
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    //region TagChipsFragmentListener
    /**
     * User removed the Tag from the Tag Chips View by closing it.
     * @param tag The [ITag] that has been removed from the
     * selected list.
     */
    override fun onTagChipClosed(tag: ITag?) {
        mHasChanges = true
        if (mListFragment != null) {
            mListFragment!!.deselectTag(tag!!)
        }
    }
    //endregion

    //region TagListFragmentListener

    /**
     * Notifies listeners a Tag has been selected.
     * @param tag The newly selected [ITag].
     */
    override fun onTagSelected(tag: ITag) {
        mHasChanges = true
        if (mChipsFragment != null) {
            mChipsFragment!!.addTag(tag)
        }
    }

    /**
     * Notifies listeners a Tag has been de-selected.
     * @param tag The newly de-selected [ITag].
     */
    override fun onTagDeselected(tag: ITag) {
        mHasChanges = true
        if (mChipsFragment != null) {
            mChipsFragment!!.removeTag(tag)
        }
    }
    //endregion

    //region Search
    /**
     * {@inheritDoc}
     */
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    /**
     * {@inheritDoc}
     */
    override fun onQueryTextChange(newText: String): Boolean {
        if (mChipsFragment != null) {
            mChipsFragment!!.onQueryTextChange(newText)
        }
        if (mListFragment != null) {
            mListFragment!!.onQueryTextChange(newText)
        }
        return true
    }
    //endregion
}
