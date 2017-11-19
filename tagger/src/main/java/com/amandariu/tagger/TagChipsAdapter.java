package com.amandariu.tagger;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author amandariu (11/18/17)
 */
public class TagChipsAdapter extends RecyclerView.Adapter<TagChipsAdapter.ViewHolder> {

    private List<Tag> mTags;
    private TagChipView.TagChipListener mTagChipListener;

    public TagChipsAdapter(@Nullable List<Tag> tags, TagChipView.TagChipListener listener) {
        if (tags == null) {
            mTags = new ArrayList<>();
        } else {
            mTags = tags;
        }
        mTagChipListener = listener;
    }

    public void add(int position, Tag tag) {
        mTags.add(position, tag);
        notifyItemRangeInserted(position, 1);
    }

    public void remove(Tag tag) {
        int pos = mTags.indexOf(tag);
        if (pos >= 0) {
            remove(pos);
        }
    }

    public void remove(int position) {
        mTags.remove(position);
        notifyItemRemoved(position);
    }

    public List<Tag> getSelectedTags() {
        return mTags;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_item_tag_chip, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Tag tag = mTags.get(position);
        holder.view.setChipTag(tag);
        holder.view.setOnTagDeletedListener(new TagChipView.TagChipListener() {
            @Override
            public void onTagClosed(TagChipView chip) {
                remove(holder.getAdapterPosition());
                if (mTagChipListener != null) {
                    mTagChipListener.onTagClosed(chip);
                }
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TagChipView view;

        public ViewHolder(View v) {
            super(v);
            view = v.findViewById(R.id.tagView);
        }
    }
}
