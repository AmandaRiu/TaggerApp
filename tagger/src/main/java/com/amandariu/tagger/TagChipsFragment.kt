package com.amandariu.tagger

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView

import java.util.ArrayList

/**
 * A fragment displaying a list of all selected Tags each drawn as a [TagChipView].
 * Allows for removing a single Tag by clicking on it.
 *
 * Activities containing this fragment MUST implement the [TagChipsFragmentListener]
 * to be notified of changes to tag selection.
 *
 * Use the [.newInstance] method for creating an instance of this Fragment.
 *
 * @see TagChipsAdapter The adapter that provides the data backing for this fragment.
 * @see TagChipView The UI representation of a single selected Tag.
 * @see TagChipsLayout The layout that manages the display of {@link TagChipView}
 *
 * @author Amanda Riu
 */
class TagChipsFragment : Fragment(), TagChipView.TagChipListener, SearchView.OnQueryTextListener {

    private var mListener: TagChipsFragmentListener? = null
    private var mAdapter: TagChipsAdapter? = null

    companion object {
        val TAG = TagChipsFragment::class.java.simpleName

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param selectedTags Tags already selected
         * @return A properly initialized [TagChipsFragment].
         */
        fun newInstance(selectedTags: List<ITag>): TagChipsFragment {
            val fragment = TagChipsFragment()
            val args = Bundle()
            args.putParcelableArray(
                    TaggerActivity.ARG_SELECTED_TAGS,
                    selectedTags.toTypedArray())
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * @return The active list of selected tags.
     */
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

        val v = inflater.inflate(R.layout.fragment_tag_chips, container, false)
        val layout = v.findViewById<TagChipsLayout>(R.id.tagChipsLayout)

        val selectedTags = ArrayList<ITag>()
        if (savedInstanceState == null) {
            if (arguments == null) {
                throw IllegalArgumentException("Selected Tags must be included in" +
                        " the arguments for this fragment. Please use the newInstance(...) method for" +
                        " proper instantiation.")
            }
            val pSelTags = arguments!!.getParcelableArray(TaggerActivity.ARG_SELECTED_TAGS)
            if (pSelTags != null && pSelTags.size > 0) {
                for (p in pSelTags) {
                    if (p !is ITag) {
                        throw ClassCastException("Invalid Array of Selected Tags. " + "The tags MUST extend ITag!")
                    }
                    selectedTags.add(p)
                }
            }
        } else {
            val pSelList = savedInstanceState.getParcelableArray(TaggerActivity.ARG_SELECTED_TAGS)
            if (pSelList != null && pSelList.size > 0) {
                for (p in pSelList) {
                    selectedTags.add(p as ITag)
                }
            }
        }
        mAdapter = TagChipsAdapter(selectedTags, this)
        layout.adapter = mAdapter

        return v
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val selectedTags = mAdapter!!.selectedTags
        if (selectedTags.size > 0) {
            outState.putParcelableArray(TaggerActivity.ARG_SELECTED_TAGS,
                    selectedTags.toTypedArray())
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * {@inheritDoc}
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TagChipsFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement TagChipsFragmentListener")
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
     * Notify listeners that the tag has been closed.
     * @param chip The chip representing the tag that has been closed.
     */
    override fun onTagClosed(chip: TagChipView) {
        if (mListener != null) {
            mListener!!.onTagChipClosed(chip.chipTag)
        }
    }

    override fun onDestroyView() {
        mAdapter = null
        super.onDestroyView()
    }

    /**
     * Add a tag to the selected tags.
     * @param tag The tag to be added to the selected tags list.
     */
    fun addTag(tag: ITag) {
        mAdapter!!.add(tag)
    }

    /**
     * Remove Tag from the list of selected tags.
     * @param tag The tag to be removed from selected tags.
     */
    fun removeTag(tag: ITag) {
        mAdapter!!.remove(tag)
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
     * Listener for monitoring the closing of Tags. When a tag is closed, it has been
     * removed from the list of selected tags. This interface should be implemented by any
     * Activity that works with [TagChipsFragment].
     */
    interface TagChipsFragmentListener {
        /**
         * User removed the Tag from the Tag Chips View by closing it.
         * @param tag The [ITag] that has been removed from the
         * selected list.
         */
        fun onTagChipClosed(tag: ITag?)
    }
}
