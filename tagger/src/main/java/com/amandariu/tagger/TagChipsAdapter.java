package com.amandariu.tagger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a list of {@link ITag}s as {@link TagChipView}s.
 * Makes a call to the specified {@link TagChipView.TagChipListener} when a tag is closed
 * by the user.
 *
 * This class implements {@link Filterable} so selected tags can actively be filtered
 * in the view.
 *
 * This Fragment may be used in standalone mode to display a list of selected tags. To
 * get the final list of selected tags use {@link #getSelectedTags()}.
 *
 * @author Amanda Riu (11/18/17)
 */
public class TagChipsAdapter extends RecyclerView.Adapter<TagChipsAdapter.ViewHolder>
        implements Filterable {

    private final List<ITag> mSelectedTags;
    private TagChipView.TagChipListener mTagChipListener;
    //
    // Filtering
    private List<ITag> mFilteredSelectedTags;
    private TagFilter mTagFilter;
    private boolean mFilterEnabled = false;

    /**
     * Constructor.
     * @param tags A list of selected tags. Can be an empty list.
     * @param listener The listener to notify when the user closes a tag to deselect it.
     */
    TagChipsAdapter(@NonNull List<ITag> tags, @Nullable TagChipView.TagChipListener listener) {
        mSelectedTags = tags;
        mFilteredSelectedTags = tags;
        mTagChipListener = listener;
    }

    /**
     * Add a new tag to the list of selected tags. This will create a new {@link TagChipView}
     * for this tag.
     * @param tag The tag to add to the selected tags.
     */
    public void add(@NonNull ITag tag) {
        mSelectedTags.add(tag);

        if (mFilterEnabled) {
            int pos = mFilteredSelectedTags.size();
            mFilteredSelectedTags.add(pos, tag);
            notifyItemRangeInserted(pos, 1);
        } else {
            notifyItemRangeInserted(mSelectedTags.size()-1, 1);
        }
    }

    /**
     * Remove a tag from the list of selected tags. This will remove the {@link TagChipView}
     * from the associated layout.
     * @param tag The tag to remove from the selected tags.
     */
    void remove(@NonNull ITag tag) {
        int pos = mSelectedTags.indexOf(tag);
        if (pos >= 0) {
            mSelectedTags.remove(pos);
        }
        if (mFilterEnabled) {
            int filteredPos = mFilteredSelectedTags.indexOf(tag);
            if (filteredPos >= 0) {
                mFilteredSelectedTags.remove(filteredPos);
                notifyItemRemoved(filteredPos);
            }
        } else {
            notifyItemRemoved(pos);
        }
    }

    /**
     * @return The active list of selected tags.
     */
    @NonNull
    List<ITag> getSelectedTags() {
        return mSelectedTags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_item_tag_chip, parent, false);
        return new ViewHolder(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ITag tag = mFilteredSelectedTags.get(position);
        holder.view.setChipTag(tag);
        holder.view.setOnTagDeletedListener(new TagChipView.TagChipListener() {
            @Override
            public void onTagClosed(TagChipView chip) {
                remove(tag);
                if (mTagChipListener != null) {
                    mTagChipListener.onTagClosed(chip);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return mFilteredSelectedTags.size();
    }


    //region Filtering
    /**
     * {@inheritDoc}
     */
    @Override
    public Filter getFilter() {
        if (mTagFilter == null) {
            mTagFilter = new TagFilter();
        }
        return mTagFilter;
    }

    /**
     * Custom class for implementing filtering by tag label behavior.
     */
    private class TagFilter extends Filter {
        /**
         * {@inheritDoc}
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<ITag> tempList = new ArrayList<>();

                for (ITag tag : mSelectedTags) {
                    if (tag.getLabel().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(tag);
                    }
                }
                mFilterEnabled = true;
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                mFilterEnabled = false;
                filterResults.count = mSelectedTags.size();
                filterResults.values = mSelectedTags;
            }
            return filterResults;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredSelectedTags = (List<ITag>)results.values;
            notifyDataSetChanged();
        }
    }
    //endregion


    class ViewHolder extends RecyclerView.ViewHolder {
        TagChipView view;

        ViewHolder(View v) {
            super(v);
            view = v.findViewById(R.id.tagView);
        }
    }
}
