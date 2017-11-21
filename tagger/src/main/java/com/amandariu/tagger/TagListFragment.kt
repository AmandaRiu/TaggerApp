package com.amandariu.tagger

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

/**
 * A fragment representing a list of all available Tags with the ability to toggle
 * tag selection.
 *
 * Activities containing this fragment MUST implement the [TagListFragmentListener]
 * to be notified of changes to tag selection.
 *
 * Use the [.newInstance] for creating a new instance of this Fragment.
 *
 * This Fragment may be used in Standalone mode to display a list of available tags and then
 * query this fragment for the list of selected tags using [.getSelectedTags].
 *
 * @see TagsListAdapter The adapter that provides the data backing for this fragment.
 *
 * @author Amanda Riu
 */
class TagListFragment : Fragment(), SearchView.OnQueryTextListener {

    companion object {
        val TAG = TagListFragment::class.java.simpleName

        /**
         * Creates a new instance of this Fragment class and initializes the arguments with the
         * required data.
         *
         * @param availTags A list containing all Tags available for selection, regardless of
         * selected state.
         * @param selectedTags A list containing only the selected Tags.
         * @return A properly initialized [TagListFragment].
         */
        fun newInstance(availTags: List<ITag>,
                        selectedTags: List<ITag>): TagListFragment {
            val fragment = TagListFragment()
            val args = Bundle()
            args.putParcelableArray(
                    TaggerActivity.ARG_AVAILABLE_TAGS,
                    availTags.toTypedArray())
            args.putParcelableArray(
                    TaggerActivity.ARG_SELECTED_TAGS,
                    selectedTags.toTypedArray())
            fragment.arguments = args
            return fragment
        }
    }

    private var mListener: TagListFragmentListener? = null
    private var mAdapter: TagsListAdapter? = null

    /**
     * @return The active list of selected tags.
     */
    @Suppress("UNUSED")
    val selectedTags: List<ITag>
        get() = mAdapter!!.selectedTags

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    /**
     * {@inheritDoc}
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)
        val context = view.context

        val selectedTags = ArrayList<ITag>()
        val availableTags = ArrayList<ITag>()
        val pSelTags: Array<Parcelable>?
        val pAvaTags: Array<Parcelable>?

        if (savedInstanceState == null) {
            if (arguments == null) {
                throw IllegalArgumentException("Selected and Available Tags must be included in" +
                        " the arguments for this fragment. Please use the newInstance(...) method for" +
                        " instantiation.")
            }
            pSelTags = arguments!!.getParcelableArray(TaggerActivity.ARG_SELECTED_TAGS)
            pAvaTags = arguments!!.getParcelableArray(TaggerActivity.ARG_AVAILABLE_TAGS)
        } else {
            pSelTags = savedInstanceState.getParcelableArray(TaggerActivity.ARG_SELECTED_TAGS)
            pAvaTags = savedInstanceState.getParcelableArray(TaggerActivity.ARG_AVAILABLE_TAGS)
        }
        //
        // Populate Selected Tags
        if (pSelTags != null && pSelTags.size > 0) {
            for (p in pSelTags) {
                if (p !is ITag) {
                    throw ClassCastException("Invalid Array of Selected Tags. " +
                            "The tags MUST extend ITag!")
                }
                selectedTags.add(p)
            }
        }
        //
        // Populate Available Tags
        if (pAvaTags != null && pAvaTags.size > 0) {
            for (p in pAvaTags) {
                if (p !is ITag) {
                    throw ClassCastException("Invalid Array of Available Tags. "
                            + "The tags MUST extend ITag!")
                }
                availableTags.add(p)
            }
        }
        mAdapter = TagsListAdapter(availableTags, selectedTags, mListener)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val selectedTags = mAdapter!!.selectedTags
        val availTags = mAdapter!!.availableTags
        if (selectedTags.size > 0) {
            outState.putParcelableArray(TaggerActivity.ARG_SELECTED_TAGS,
                    selectedTags.toTypedArray())
        }
        outState.putParcelableArray(TaggerActivity.ARG_AVAILABLE_TAGS,
                availTags.toTypedArray())
        super.onSaveInstanceState(outState)
    }

    /**
     * {@inheritDoc}
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TagListFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement TagListFragmentListener")
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * Deselects the Tag in the list of available tags.
     * @param tag The tag to deselect.
     */
    fun deselectTag(tag: ITag) {
        mAdapter!!.deselectTag(tag)
    }

    override fun onDestroyView() {
        mAdapter = null
        super.onDestroyView()
    }

    //region Filtering
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
        mAdapter!!.filter.filter(newText)
        return true
    }
    //endregion

    /**
     * Listener methods for monitoring the selection and de-selection of
     * Tags. This interface should be implemented by any Activity that works
     * with [TagListFragment].
     */
    interface TagListFragmentListener {
        /**
         * Notifies listeners a Tag has been selected.
         * @param tag The newly selected [ITag].
         */
        fun onTagSelected(tag: ITag)

        /**
         * Notifies listeners a Tag has been de-selected.
         * @param tag The newly de-selected [ITag].
         */
        fun onTagDeselected(tag: ITag)
    }
}
