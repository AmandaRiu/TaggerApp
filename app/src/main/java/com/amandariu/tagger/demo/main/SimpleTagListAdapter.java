package com.amandariu.tagger.demo.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amandariu.tagger.ITag;
import com.amandariu.tagger.demo.R;

import java.util.List;

/**
 * Simple adapter for displaying tag results in the main test application.
 *
 * @author Amanda Riu
 */
public class SimpleTagListAdapter extends RecyclerView.Adapter<SimpleTagListAdapter.TagViewHolder> {

    private final List<ITag> mTags;

    public SimpleTagListAdapter(List<ITag> mTags) {
        this.mTags = mTags;
    }

    public void setTags(@NonNull List<? extends ITag> tags) {
        mTags.clear();
        for (ITag t : tags) {
            mTags.add(t);
        }
        notifyDataSetChanged();
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_tag_result, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        final ITag tag = mTags.get(position);
        holder.tagName.setText(String.format("%s (id: %d)", tag.getLabel(), tag.getId()));
        holder.rowNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        public TextView rowNumber;
        public TextView tagName;

        public TagViewHolder(View itemView) {
            super(itemView);
            this.rowNumber = itemView.findViewById(R.id.lbl_rowNum);
            this.tagName = itemView.findViewById(R.id.txt_tagName);
        }
    }
}
