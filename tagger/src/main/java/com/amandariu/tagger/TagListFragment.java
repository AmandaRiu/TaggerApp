package com.amandariu.tagger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link TagListFragmentListener}
 * interface.
 */
public class TagListFragment extends Fragment {

    public static final String AVAILABLE_TAGS = "AVAILABLE-TAGS";

    private TagListFragmentListener mListener;
    private List<Tag> mTags;
    private TagsListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TagListFragment() {
    }

    @SuppressWarnings("unused")
    public static TagListFragment newInstance(List<Tag> availableTags) {
        TagListFragment fragment = new TagListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(AVAILABLE_TAGS, (ArrayList)availableTags);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTags = getArguments().getParcelableArrayList(AVAILABLE_TAGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        mAdapter = new TagsListAdapter(mTags, mListener);
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TagListFragmentListener) {
            mListener = (TagListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TagListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void deselectTag(Tag tag) {
        mAdapter.deselectTag(tag);
    }


    public interface TagListFragmentListener {
        void onTagSelectionChanged(Tag tag);
    }
}
