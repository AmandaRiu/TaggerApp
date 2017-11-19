package com.amandariu.tagger;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amandariu.tagger.TagListFragment.TagListFragmentListener;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a list of {@link Tag}s and makes a call to the
 * specified {@link TagListFragmentListener} when a tag selection is toggled on or off.
 */
public class TagsListAdapter extends RecyclerView.Adapter<TagsListAdapter.ViewHolder> {

    private final List<ITag> mAvailableTags;
    private final List<ITag> mSelectedTags;
    private final TagListFragmentListener mListener;

    public TagsListAdapter(List<ITag> availTags, List<ITag> selectedTags, TagListFragmentListener listener) {
        mAvailableTags = availTags;
        mSelectedTags = selectedTags;
        mListener = listener;
    }

    public void deselectTag(ITag tag) {
        int pos = mAvailableTags.indexOf(tag);
        if (mSelectedTags.contains(tag)) {
            mSelectedTags.remove(tag);
        }
        notifyItemChanged(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_tag_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTag = mAvailableTags.get(position);
        holder.mTxtLabel.setText(holder.mTag.getLabel());
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

    private boolean isSelected(ITag tag) {
        return mSelectedTags.contains(tag);
    }

    @Override
    public int getItemCount() {
        return mAvailableTags.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ITag mTag;
        public TextView mTxtLabel;
        public ImageView mImgSelected;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTxtLabel = view.findViewById(R.id.txt_label);
            mImgSelected = view.findViewById(R.id.img_selected);
        }
    }
}
