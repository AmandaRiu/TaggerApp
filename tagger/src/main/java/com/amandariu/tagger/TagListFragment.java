package com.amandariu.tagger;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of all available Tags with the ability to toggle
 * tag selection.
 * <p/>
 * Activities containing this fragment MUST implement the {@link TagListFragmentListener}
 * to be notified of changes to tag selection.
 * <p/>
 * Use the {@link #newInstance(List, List)} for creating a new instance of this Fragment.
 *
 * This Fragment may be used in Standalone mode to display a list of available tags and then
 * query this fragment for the list of selected tags using {@link #getSelectedTags()}.
 *
 * @see TagsListAdapter The adapter that provides the data backing for this fragment.
 *
 * @author Amanda Riu
 */
public class TagListFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static final String TAG = TagListFragment.class.getSimpleName();
    private TagListFragmentListener mListener;
    private TagsListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TagListFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this Fragment class and initializes the arguments with the
     * required data.
     *
     * @param availTags A list containing all Tags available for selection, regardless of
     *                      selected state.
     * @param selectedTags A list containing only the selected Tags.
     * @return A properly initialized {@link TagListFragment}.
     */
    public static TagListFragment newInstance(List<? extends ITag> availTags,
                                              List<? extends ITag> selectedTags) {
        TagListFragment fragment = new TagListFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(
                TaggerActivity.ARG_AVAILABLE_TAGS,
                availTags.toArray(new ITag[availTags.size()]));
        args.putParcelableArray(
                TaggerActivity.ARG_SELECTED_TAGS,
                selectedTags.toArray(new ITag[selectedTags.size()]));
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        //
        // TODO: 11/19/17 check if we should be validating savedInstanceState
        if (getArguments() == null) {
            throw new IllegalArgumentException("Selected and Available Tags must be included in " +
                    "the arguments for this fragment. Please use the newInstance(...) method for" +
                    " instantiation.");
        }
        //
        // Populate Selected Tags
        Parcelable[] pSelTags = getArguments().getParcelableArray(TaggerActivity.ARG_SELECTED_TAGS);
        List<ITag> selectedTags = new ArrayList<>();
        if (pSelTags != null && pSelTags.length > 0) {
            for (Parcelable p : pSelTags) {
                if (!(p instanceof ITag)) {
                    throw new ClassCastException("Invalid Array of Selected Tags. " +
                            "The tags MUST extend ITag!");
                }
                selectedTags.add((ITag)p);
            }
        }
        //
        // Populate Available Tags
        Parcelable[] pAvaTags = getArguments().getParcelableArray(TaggerActivity.ARG_AVAILABLE_TAGS);
        List<ITag> availableTags = new ArrayList<>();
        if (pAvaTags != null && pAvaTags.length > 0) {
            for (Parcelable p : pAvaTags) {
                if (!(p instanceof ITag)) {
                    throw new ClassCastException("Invalid Array of Available Tags. " +
                            "The tags MUST extend ITag!");
                }
                availableTags.add((ITag)p);
            }
        }

        mAdapter = new TagsListAdapter(availableTags, selectedTags,  mListener);
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Deselects the Tag in the list of available tags.
     * @param tag The tag to deselect.
     */
    public void deselectTag(ITag tag) {
        mAdapter.deselectTag(tag);
    }

    /**
     * @return The active list of selected tags.
     */
    @SuppressWarnings("unused")
    @NonNull
    public List<ITag> getSelectedTags() {
        return mAdapter.getSelectedTags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        mAdapter = null;
        super.onDestroy();
    }


    //region Filtering
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }
    //endregion


    /**
     * Listener methods for monitoring the selection and de-selection of
     * Tags. This interface should be implemented by any Activity that works
     * with {@link TagListFragment}.
     */
    public interface TagListFragmentListener {
        /**
         * Notifies listeners a Tag has been selected.
         * @param tag The newly selected {@link ITag}.
         */
        void onTagSelected(ITag tag);

        /**
         * Notifies listeners a Tag has been de-selected.
         * @param tag The newly de-selected {@link ITag}.
         */
        void onTagDeselected(ITag tag);
    }
}
