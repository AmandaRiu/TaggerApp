package com.amandariu.tagger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView

import com.amandariu.tagger.TagListFragment.TagListFragmentListener

import java.util.ArrayList
import java.util.Collections

/**
 * [RecyclerView.Adapter] that can display a list of [ITag]s and makes a call to the
 * specified [TagListFragmentListener] when a tag selection is toggled on or off.
 *
 * This class implements [Filterable] so available tags can be actively filtered
 * in the view.
 *
 * @author Amanda Riu
 */
class TagsListAdapter
/**
 * Constructor.
 * @param availTags A list of available tags.
 * @param mSelectedTags A list of selected tags.
 * @param mListener The listener to notify when the user toggles selection on an
 * tag in the list of available tags. Can be null.
 */
internal constructor(availTags: MutableList<ITag>,
                     private val mSelectedTags: MutableList<ITag>,
                     private val mListener: TagListFragmentListener?)
    : RecyclerView.Adapter<TagsListAdapter.ViewHolder>(), Filterable {

    /**
     * @return The active list of available tags.
     */
    val availableTags: List<ITag>
    //
    // Filtering
    private var mFilteredAvailableTags: MutableList<ITag>? = null
    private var mTagFilter: TagFilter? = null
    private var mFilterEnabled = false

    /**
     * @return The active list of selected tags.
     */
    val selectedTags: List<ITag>
        get() = mSelectedTags

    init {
        Collections.sort(availTags)
        availableTags = availTags
        mFilteredAvailableTags = availTags
    }

    /**
     * Updates the state of the provided tag by removing it from teh list of
     * selected tags and telling this adapter to refresh the state of the view
     * representing this tag.
     * @param tag The tag to deselect.
     */
    fun deselectTag(tag: ITag) {
        val pos = mFilteredAvailableTags!!.indexOf(tag)
        if (mSelectedTags.contains(tag)) {
            mSelectedTags.remove(tag)
        }

        if (mFilterEnabled) {
            val filteredPos = mFilteredAvailableTags!!.indexOf(tag)
            if (filteredPos >= 0) {
                mFilteredAvailableTags!!.removeAt(filteredPos)
                notifyItemChanged(filteredPos)
            }
        } else {
            notifyItemChanged(pos)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_tag_list, parent, false)
        return ViewHolder(view)
    }

    /**
     * {@inheritDoc}
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTag = mFilteredAvailableTags!![position]
        holder.mTxtLabel.text = holder.mTag!!.label
        //
        // If this tag exists in the {@link #mSelectedTags}, then set the
        // check mark image visible. Else, hide it.
        if (isSelected(holder.mTag!!)) {
            holder.mImgSelected.visibility = View.VISIBLE
        } else {
            holder.mImgSelected.visibility = View.GONE
        }
        holder.mView.setOnClickListener {
            if (isSelected(holder.mTag!!)) {
                //
                // Tag has been deselected.
                holder.mImgSelected.visibility = View.GONE
                holder.mTag?.let {
                    mSelectedTags.remove(it)
                    mListener?.onTagDeselected(it)
                }
            } else {
                //
                // Tag has been selected.
                holder.mImgSelected.visibility = View.VISIBLE
                holder.mTag?.let {
                    mSelectedTags.add(it)
                    mListener?.onTagSelected(it)
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getItemCount(): Int {
        return mFilteredAvailableTags!!.size
    }

    /**
     * Checks the provided tag against the list of selected tags to determine if
     * the tag is selected.
     * @param tag The tag to check for selection.
     * @return True if the tag exists in the [.mSelectedTags] lists,
     * else false.
     */
    private fun isSelected(tag: ITag): Boolean {
        return mSelectedTags.contains(tag)
    }


    //region Filtering
    /**
     * {@inheritDoc}
     */
    override fun getFilter(): Filter {
        if (mTagFilter == null) {
            mTagFilter = TagFilter()
        }
        return mTagFilter!!
    }

    /**
     * Custom class for implementing filtering by tag label behavior.
     */
    private inner class TagFilter : Filter() {
        /**
         * {@inheritDoc}
         */
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val filterResults = Filter.FilterResults()
            if (constraint != null && constraint.length > 0) {
                val tempList = ArrayList<ITag>()

                for (tag in availableTags) {
                    if (tag.label.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(tag)
                    }
                }
                mFilterEnabled = true
                filterResults.count = tempList.size
                filterResults.values = tempList
            } else {
                mFilterEnabled = false
                filterResults.count = availableTags.size
                filterResults.values = availableTags
            }
            return filterResults
        }

        /**
         * {@inheritDoc}
         */
        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            mFilteredAvailableTags = results.values as MutableList<ITag>
            notifyDataSetChanged()
        }
    }
    //endregion


    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mTag: ITag? = null
        val mTxtLabel: TextView
        val mImgSelected: ImageView

        init {
            mTxtLabel = mView.findViewById(R.id.txt_label)
            mImgSelected = mView.findViewById(R.id.img_selected)
        }
    }
}
