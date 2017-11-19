package com.amandariu.tagger;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author amandariu (11/18/17)
 */
public class TagChipsAdapter extends RecyclerView.Adapter<TagChipsAdapter.ViewHolder> {

    private final List<ITag> mSelectedTags;
    private TagChipView.TagChipListener mTagChipListener;

    public TagChipsAdapter(@NonNull List<ITag> tags, @NonNull TagChipView.TagChipListener listener) {
        mSelectedTags = tags;
        mTagChipListener = listener;
    }

    public void add(int position, ITag tag) {
        mSelectedTags.add(position, tag);
        notifyItemRangeInserted(position, 1);
    }

    public void remove(ITag tag) {
        int pos = mSelectedTags.indexOf(tag);
        if (pos >= 0) {
            remove(pos);
        }
    }

    public void remove(int position) {
        mSelectedTags.remove(position);
        notifyItemRemoved(position);
    }

    public List<ITag> getSelectedTags() {
        return mSelectedTags;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_item_tag_chip, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ITag tag = mSelectedTags.get(position);
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
        return mSelectedTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TagChipView view;

        public ViewHolder(View v) {
            super(v);
            view = v.findViewById(R.id.tagView);
        }
    }
}
