package com.amandariu.tagger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable

import java.util.ArrayList
import java.util.Collections

/**
 * [RecyclerView.Adapter] that can display a list of [ITag]s as [TagChipView]s.
 * Makes a call to the specified [TagChipView.TagChipListener] when a tag is closed
 * by the user.
 *
 * This class implements [Filterable] so selected tags can actively be filtered
 * in the view.
 *
 * This Fragment may be used in standalone mode to display a list of selected tags. To
 * get the final list of selected tags use [.getSelectedTags].
 *
 * @author Amanda Riu
 */
class TagChipsAdapter
/**
 * Constructor.
 * @param mSelectedTags A list of selected tags. Can be an empty list.
 * @param mTagChipListener The listener to notify when the user closes a tag to deselect it.
 */
internal constructor(private val mSelectedTags: MutableList<ITag>,
                     private val mTagChipListener: TagChipView.TagChipListener?)
    : RecyclerView.Adapter<TagChipsAdapter.ViewHolder>(), Filterable {
    //
    // Filtering
    private var mFilteredSelectedTags: MutableList<ITag>? = null
    private var mTagFilter: TagFilter? = null
    private var mFilterEnabled = false

    /**
     * @return The active list of selected tags.
     */
    val selectedTags: List<ITag>
        get() = mSelectedTags

    init {

        Collections.sort(mSelectedTags)
        mFilteredSelectedTags = mSelectedTags
    }

    /**
     * Add a new tag to the list of selected tags. This will create a new [TagChipView]
     * for this tag.
     * @param tag The tag to add to the selected tags.
     */
    fun add(tag: ITag) {
        val pos = getAlphabeticalPos(mSelectedTags, tag)
        mSelectedTags.add(pos, tag)

        if (mFilterEnabled) {
            val filterPos = getAlphabeticalPos(mFilteredSelectedTags!!, tag)
            mFilteredSelectedTags!!.add(filterPos, tag)
            notifyItemRangeInserted(filterPos, 1)
        } else {
            notifyItemRangeInserted(pos, 1)
        }
    }

    /**
     * Get the position to insert the tag so the list of selected tags will continue
     * to be in alphabetical order.
     * @param tagList The list to evaluate.
     * @param tag The tag to find a position for.
     * @return The position the tag should be inserted.
     */
    private fun getAlphabeticalPos(tagList: List<ITag>, tag: ITag): Int {
        for (i in tagList.indices) {
            if (tagList[i].compareTo(tag) > 0) {
                return i
            }
        }
        return tagList.size
    }


    /**
     * Remove a tag from the list of selected tags. This will remove the [TagChipView]
     * from the associated layout.
     * @param tag The tag to remove from the selected tags.
     */
    fun remove(tag: ITag) {
        val pos = mSelectedTags.indexOf(tag)
        if (pos >= 0) {
            mSelectedTags.removeAt(pos)
        }
        if (mFilterEnabled) {
            val filteredPos = mFilteredSelectedTags!!.indexOf(tag)
            if (filteredPos >= 0) {
                mFilteredSelectedTags!!.removeAt(filteredPos)
                notifyItemRemoved(filteredPos)
            }
        } else {
            notifyItemRemoved(pos)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_item_tag_chip, parent, false)
        return ViewHolder(v)
    }

    /**
     * {@inheritDoc}
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = mFilteredSelectedTags!![position]
        holder.view.chipTag = tag
        holder.view.setOnTagDeletedListener(TagChipListenerImpl())
    }

    /**
     * Inner class to get around 'type mismatch' issue with kotlin and
     * interfaces.
     */
    inner class TagChipListenerImpl : TagChipView.TagChipListener {
        /**
         * User has clicked the chip to remove it from the selected
         * tags list.
         */
        override fun onTagClosed(chip: TagChipView) {
            chip.chipTag?.let {
                remove(it)
            }
            mTagChipListener?.onTagClosed(chip)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getItemCount(): Int {
        return mFilteredSelectedTags!!.size
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

                for (tag in mSelectedTags) {
                    if (tag.label.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(tag)
                    }
                }
                mFilterEnabled = true
                filterResults.count = tempList.size
                filterResults.values = tempList
            } else {
                mFilterEnabled = false
                filterResults.count = mSelectedTags.size
                filterResults.values = mSelectedTags
            }
            return filterResults
        }

        /**
         * {@inheritDoc}
         */
        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            mFilteredSelectedTags = results.values as MutableList<ITag>
            notifyDataSetChanged()
        }
    }
    //endregion


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view: TagChipView

        init {
            view = v.findViewById(R.id.tagView)
        }
    }
}
