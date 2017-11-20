package com.amandariu.tagger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amandariu.tagger.TagListFragment.TagListFragmentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a list of {@link ITag}s and makes a call to the
 * specified {@link TagListFragmentListener} when a tag selection is toggled on or off.
 *
 * This class implements {@link Filterable} so available tags can be actively filtered
 * in the view.
 *
 * @author Amanda Riu (11/18/17)
 */
public class TagsListAdapter extends RecyclerView.Adapter<TagsListAdapter.ViewHolder>
        implements Filterable {

    private final List<ITag> mAvailableTags;
    private final List<ITag> mSelectedTags;
    private final TagListFragmentListener mListener;
    //
    // Filtering
    private List<ITag> mFilteredAvailableTags;
    private TagFilter mTagFilter;
    private boolean mFilterEnabled = false;

    /**
     * Constructor.
     * @param availTags A list of available tags.
     * @param selectedTags A list of selected tags.
     * @param listener The listener to notify when the user toggles selection on an
     *                 tag in the list of available tags. Can be null.
     */
    TagsListAdapter(@NonNull List<ITag> availTags,
                           @NonNull List<ITag> selectedTags,
                           @Nullable TagListFragmentListener listener) {
        mAvailableTags = availTags;
        mFilteredAvailableTags = availTags;
        mSelectedTags = selectedTags;
        mListener = listener;
    }

    /**
     * @return The active list of selected tags.
     */
    @NonNull
    List<ITag> getSelectedTags() {
        return mSelectedTags;
    }

    /**
     * Updates the state of the provided tag by removing it from teh list of
     * selected tags and telling this adapter to refresh the state of the view
     * representing this tag.
     * @param tag The tag to deselect.
     */
    void deselectTag(@NonNull ITag tag) {
        int pos = mFilteredAvailableTags.indexOf(tag);
        if (mSelectedTags.contains(tag)) {
            mSelectedTags.remove(tag);
        }

        if (mFilterEnabled) {
            int filteredPos = mFilteredAvailableTags.indexOf(tag);
            if (filteredPos >=0) {
                mFilteredAvailableTags.remove(filteredPos);
                notifyItemChanged(filteredPos);
            }
        } else {
            notifyItemChanged(pos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_tag_list, parent, false);
        return new ViewHolder(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTag = mFilteredAvailableTags.get(position);
        holder.mTxtLabel.setText(holder.mTag.getLabel());
        //
        // If this tag exists in the {@link #mSelectedTags}, then set the
        // check mark image visible. Else, hide it.
        if (isSelected(holder.mTag)) {
            holder.mImgSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mImgSelected.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected(holder.mTag)) {
                    //
                    // Tag has been deselected.
                    holder.mImgSelected.setVisibility(View.GONE);
                    mSelectedTags.remove(holder.mTag);
                    if (null != mListener) {
                        mListener.onTagDeselected(holder.mTag);
                    }
                } else {
                    //
                    // Tag has been selected.
                    holder.mImgSelected.setVisibility(View.VISIBLE);
                    mSelectedTags.add(holder.mTag);
                    if (null != mListener) {
                        mListener.onTagSelected(holder.mTag);
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return mFilteredAvailableTags.size();
    }

    /**
     * Checks the provided tag against the list of selected tags to determine if
     * the tag is selected.
     * @param tag The tag to check for selection.
     * @return True if the tag exists in the {@link #mSelectedTags} lists,
     * else false.
     */
    private boolean isSelected(@NonNull ITag tag) {
        return mSelectedTags.contains(tag);
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

                for (ITag tag : mAvailableTags) {
                    if (tag.getLabel().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(tag);
                    }
                }
                mFilterEnabled = true;
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                mFilterEnabled = false;
                filterResults.count = mAvailableTags.size();
                filterResults.values = mAvailableTags;
            }
            return filterResults;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredAvailableTags = (List<ITag>)results.values;
            notifyDataSetChanged();
        }
    }
    //endregion


    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        ITag mTag;
        TextView mTxtLabel;
        ImageView mImgSelected;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTxtLabel = view.findViewById(R.id.txt_label);
            mImgSelected = view.findViewById(R.id.img_selected);
        }
    }
}
