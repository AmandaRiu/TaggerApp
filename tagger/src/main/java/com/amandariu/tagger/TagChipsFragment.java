package com.amandariu.tagger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagChipsFragment.TagChipsFragmentListener} interface
 * to handle interaction events.
 * Use the {@link TagChipsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagChipsFragment extends Fragment implements TagChipView.TagChipListener {

    private static final String SELECTED_TAGS = "SELECTED-TAGS";

    private TagChipsFragmentListener mListener;
    private TagChipsAdapter mAdapter;

    public TagChipsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param selectedTags Tags already selected
     * @return A new instance of fragment TagChipsFragment.
     */
    public static TagChipsFragment newInstance(List<Tag> selectedTags) {
        TagChipsFragment fragment = new TagChipsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(SELECTED_TAGS, (ArrayList)selectedTags);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tag_chips, container, false);
        TagChipsLayout layout = v.findViewById(R.id.tagChipsLayout);
        if (getArguments() != null) {
            List<Tag> selectedTags = getArguments().getParcelableArrayList(SELECTED_TAGS);
            mAdapter = new TagChipsAdapter(selectedTags, this);
            layout.setAdapter(mAdapter);
        }
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TagChipsFragmentListener) {
            mListener = (TagChipsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TagChipsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onTagClosed(TagChipView chip) {
        if (mListener != null) {
            mListener.onTagRemoved(chip.getChipTag());
        }
    }

    @Override
    public void onDestroy() {
        mAdapter = null;

        super.onDestroy();
    }

    public void addTag(Tag tag) {
        mAdapter.add(mAdapter.getItemCount(), tag);
    }

    public void removeTag(Tag tag) {
        mAdapter.remove(tag);
    }

    public interface TagChipsFragmentListener {
        void onTagRemoved(Tag tag);
    }

    public List<Tag> getSelectedTags() {
        return mAdapter.getSelectedTags();
    }
}
