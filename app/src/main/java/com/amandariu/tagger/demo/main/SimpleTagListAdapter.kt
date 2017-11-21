package com.amandariu.tagger.demo.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.amandariu.tagger.ITag
import com.amandariu.tagger.demo.R

/**
 * Simple adapter for displaying tag results in the main test application.
 *
 * @author Amanda Riu
 */
class SimpleTagListAdapter(private val mTags: MutableList<ITag>)
    : RecyclerView.Adapter<SimpleTagListAdapter.TagViewHolder>() {

    val tags: List<ITag>
        get() = mTags

    @Suppress("UNUSED")
    fun setTags(tags: List<ITag>) {
        mTags.clear()
        mTags.addAll(tags)
        notifyDataSetChanged()
    }

    fun clearTags() {
        mTags.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_tag_result, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = mTags[position]
        holder.tagName.text = String.format("%s (id: %d)", tag.label, tag.id)
        holder.rowNumber.text = (position + 1).toString()
    }

    override fun getItemCount(): Int {
        return mTags.size
    }

    class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowNumber: TextView
        val tagName: TextView

        init {
            this.rowNumber = itemView.findViewById(R.id.lbl_rowNum)
            this.tagName = itemView.findViewById(R.id.txt_tagName)
        }
    }
}
