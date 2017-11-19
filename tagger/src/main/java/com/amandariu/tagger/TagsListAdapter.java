package com.amandariu.tagger;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amandariu.tagger.TagListFragment.TagListFragmentListener;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Tag} and makes a call to the
 * specified {@link TagListFragmentListener}.
 */
public class TagsListAdapter extends RecyclerView.Adapter<TagsListAdapter.ViewHolder> {

    private final List<Tag> mTags;
    private final TagListFragmentListener mListener;

    public TagsListAdapter(List<Tag> items, TagListFragmentListener listener) {
        mTags = items;
        mListener = listener;
    }

    public void deselectTag(Tag tag) {
        for (int i = 0; i < mTags.size(); i++) {
            if (mTags.get(i).getId() == tag.getId()) {
                Tag matchingTag = mTags.get(i);
                if (matchingTag.isSelected()) {
                    matchingTag.toggleSelected();
                }
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_tag_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTag = mTags.get(position);
        holder.mImgSelected.setVisibility(holder.mTag.isSelected() ? View.VISIBLE : View.GONE);
        holder.mTxtLabel.setText(holder.mTag.getLabel());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mTag.toggleSelected();
                holder.mImgSelected.setVisibility(holder.mTag.isSelected() ? View.VISIBLE : View.GONE);
                if (null != mListener) {
                    mListener.onTagSelectionChanged(holder.mTag);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Tag mTag;
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
