package com.amandariu.tagger.demo.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.test.espresso.IdlingResource
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.amandariu.tagger.ITag
import com.amandariu.tagger.TaggerActivity
import com.amandariu.tagger.demo.R
import com.amandariu.tagger.demo.Injection
import com.amandariu.tagger.demo.utils.EspressoIdlingResource
import com.amandariu.tagger.demo.utils.displayErrorMessage
import com.amandariu.tagger.demo.utils.hasMinimumApi
import java.util.ArrayList

class MainActivity : AppCompatActivity(), MainContract.View, View.OnClickListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private var mPresenter: MainContract.Presenter? = null
    //
    // View components
    private var mAvailableTagsAdapter: SimpleTagListAdapter? = null
    private var mSelectedTagsAdapter: SimpleTagListAdapter? = null
    private var mViewLoading: ViewGroup? = null
    private var mNoAvailableTags: TextView? = null
    private var mNoSelectedTags: TextView? = null
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        // Create the presenter
        MainPresenter(Injection.provideTagsRepository(applicationContext), this)
        //
        // Initialize View Components
        //
        // Get Tags from the best available option
        val btnGetTags = findViewById<Button>(R.id.btn_loadTags)
        btnGetTags.setOnClickListener(this)
        //
        // Get Tags from API
        val btnGetApi = findViewById<Button>(R.id.btn_api)
        btnGetApi.setOnClickListener(this)
        //
        // Get Tags from Database
        val btnGetDb = findViewById<Button>(R.id.btn_database)
        btnGetDb.setOnClickListener(this)
        //
        // Open the Tag Selector
        val btnSelectTags = findViewById<Button>(R.id.btn_selectTags)
        btnSelectTags.setOnClickListener(this)
        //
        // Display Available Tags
        mNoAvailableTags = findViewById(R.id.txt_noAvailableTags)
        val availList = findViewById<RecyclerView>(R.id.list_availableTags)
        mAvailableTagsAdapter = SimpleTagListAdapter(ArrayList())
        availList.adapter = mAvailableTagsAdapter
        val clearAvailableTags = findViewById<Button>(R.id.btn_clearAvailableTags)
        clearAvailableTags.setOnClickListener(this)
        //
        // Display Selected Tags
        mNoSelectedTags = findViewById(R.id.txt_noSelectedTags)
        val selectList = findViewById<RecyclerView>(R.id.list_selectedTags)
        mSelectedTagsAdapter = SimpleTagListAdapter(ArrayList())
        selectList.adapter = mSelectedTagsAdapter
        val clearSelectedTags = findViewById<Button>(R.id.btn_clearSelectedTags)
        clearSelectedTags.setOnClickListener(this)
        //
        // Loading indicator
        mViewLoading = findViewById(R.id.view_loading)

        if (savedInstanceState != null) {
            //
            // Restore any selected tags saved during configuration change.
            val selectedTags = ArrayList<ITag>()
            val pSelTags = savedInstanceState
                    .getParcelableArray(TaggerActivity.ARG_SELECTED_TAGS)
            if (pSelTags != null && pSelTags.size > 0) {
                for (t in pSelTags) {
                    selectedTags.add(t as ITag)
                }
                setSelectedTags(selectedTags)
            }
            //
            // Restore available tags saved during configuration change
            val pAvaTags = savedInstanceState
                    .getParcelableArray(TaggerActivity.ARG_AVAILABLE_TAGS)
            if (pAvaTags != null && pAvaTags.size > 0) {
                val tags = ArrayList<ITag>(pAvaTags.size)
                for (t in pAvaTags) {
                    tags.add(t as ITag)
                }
                setAvailableTags(tags)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        //
        // Save selected tags
        val selectedTags = mSelectedTagsAdapter!!.tags
        if (selectedTags.size > 0) {
            outState!!.putParcelableArray(
                    TaggerActivity.ARG_SELECTED_TAGS,
                    selectedTags.toTypedArray())
        }
        //
        // Save available tags
        val availableTags = mAvailableTagsAdapter!!.tags
        if (availableTags.size > 0) {
            outState!!.putParcelableArray(
                    TaggerActivity.ARG_AVAILABLE_TAGS,
                    availableTags.toTypedArray())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        //        mPresenter.start();
    }

    override fun onDestroy() {
        mPresenter!!.destroyView()
        mViewLoading = null
        mAvailableTagsAdapter = null
        mNoSelectedTags = null
        mNoAvailableTags = null
        mSelectedTagsAdapter = null
        super.onDestroy()
    }

    //region MainContract.View
    /**
     * Set the presenter for the view.
     * @param presenter The presenter for this view.
     */
    override fun setPresenter(presenter: MainContract.Presenter) {
        mPresenter = presenter
    }

    /**
     * Request the loading indicator be displayed or hidden.
     * @param active If true, show the loading indicator. If false, hide it.
     */
    override fun setLoadingIndicator(active: Boolean) {
        mViewLoading!!.visibility = if (active) View.VISIBLE else View.GONE
    }

    /**
     * Update the view with the available tags provided.
     * @param tags The tags fetched from the Repo.
     */
    override fun setAvailableTags(tags: List<ITag>?) {
        Log.d(TAG, "Updating view with available tags ["
                + (tags?.size ?: "null") + "]")
        if (tags != null) {
            mAvailableTagsAdapter!!.setTags(tags)
        }
        if (tags != null && tags.size > 0) {
            showNoAvailableTags(false)
        } else {
            showNoAvailableTags(true)
        }
    }

    /**
     * Update the view with the selected tags provided. These are the tags
     * the user selected using the custom Tag selector.
     *
     * @param tags A list of tags the user selected.
     */
    override fun setSelectedTags(tags: List<ITag>?) {
        Log.d(TAG, "Updating view with selected tags ["
                + (tags?.size ?: "null") + "]")

        if (tags != null && !tags.isEmpty()) {
            showNoSelectedTags(false)
            mSelectedTagsAdapter!!.setTags(tags)
        } else {
            showNoSelectedTags(true)
        }
    }

    /**
     * Show the "no available tags for display" view.
     *
     * @param on True if the No Available Tags view should be displayed, else false.
     */
    override fun showNoAvailableTags(on: Boolean) {
        if (on) {
            mAvailableTagsAdapter!!.clearTags()
            mNoAvailableTags!!.visibility = View.VISIBLE
        } else {
            mNoAvailableTags!!.visibility = View.GONE
        }
    }

    /**
     * Show the "no selected tags for display" view.
     *
     * @param on True if the No Selected Tags view should be displayed, else false.
     */
    override fun showNoSelectedTags(on: Boolean) {
        if (on) {
            mNoSelectedTags!!.visibility = View.VISIBLE
        } else {
            mNoSelectedTags!!.visibility = View.GONE
        }
    }

    /**
     * Display an error message to the end user.
     *
     * @param msg The message to be displayed.
     */
    override fun showError(msg: String) {
        displayErrorMessage(this, msg)
    }

    /**
     * @return True if the view is still active, else false.
     */
    override val isActive: Boolean
        get() = !hasMinimumApi(17) || !isDestroyed

    //endregion

    //region ClickListener
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_loadTags -> {
                showNoAvailableTags(true)
                mPresenter!!.loadTags(false)
            }
            R.id.btn_api -> {
                showNoAvailableTags(true)
                mPresenter!!.loadTagsFromRemote(this@MainActivity)
            }
            R.id.btn_database -> {
                showNoAvailableTags(true)
                mPresenter!!.loadTagsFromLocal()
            }
            R.id.btn_selectTags -> {
                val availableTags = mAvailableTagsAdapter!!.tags
                val selectedTags = mSelectedTagsAdapter!!.tags
                if (availableTags.size > 0) {
                    mPresenter!!.selectTags(this, availableTags, selectedTags)
                } else {
                    Toast.makeText(
                            this,
                            R.string.no_tags_loaded,
                            Toast.LENGTH_LONG).show()
                }
            }
            R.id.btn_clearAvailableTags -> {
                mAvailableTagsAdapter!!.clearTags()
                showNoAvailableTags(true)
            }
            R.id.btn_clearSelectedTags -> {
                mSelectedTagsAdapter!!.clearTags()
                showNoSelectedTags(true)
            }
            else -> Log.e(TAG, "Unknown view sent to onClick handler!")
        }
    }
    //endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == TaggerActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val pSelTags = data.getParcelableArrayExtra(TaggerActivity.ARG_SELECTED_TAGS)
                if (pSelTags != null && pSelTags.size > 0) {
                    val selectedTags = ArrayList<ITag>()
                    for (p in pSelTags) {
                        selectedTags.add(p as ITag)
                    }
                    setSelectedTags(selectedTags)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //region Testing
    @Suppress("UNUSED")
    val countingIdlingResource: IdlingResource
        @VisibleForTesting
        get() = EspressoIdlingResource.idlingResource
    //endregion
}
