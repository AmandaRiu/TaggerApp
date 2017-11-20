package com.amandariu.tagger;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment displaying a list of all selected Tags each drawn as a {@link TagChipView}.
 * Allows for removing a single Tag by clicking on it.
 * <p/>
 * Activities containing this fragment MUST implement the {@link TagChipsFragmentListener}
 * to be notified of changes to tag selection.
 * <p/>
 * Use the {@link #newInstance(List)} method for creating an instance of this Fragment.
 *
 * @see TagChipsAdapter The adapter that provides the data backing for this fragment.
 * @see TagChipView The UI representation of a single selected Tag.
 * @see TagChipsLayout The layout that manages the display of {@link TagChipView}
 *
 * @author Amanda Riu
 */
public class TagChipsFragment extends Fragment
        implements TagChipView.TagChipListener, SearchView.OnQueryTextListener {

    public static final String TAG = TagChipsFragment.class.getSimpleName();
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
     * @return A properly initialized {@link TagChipsFragment}.
     */
    public static TagChipsFragment newInstance(List<? extends ITag> selectedTags) {
        TagChipsFragment fragment = new TagChipsFragment();
        Bundle args = new Bundle();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tag_chips, container, false);
        TagChipsLayout layout = v.findViewById(R.id.tagChipsLayout);
        if (getArguments() == null) {
            throw new IllegalArgumentException("Selected Tags must be included in" +
                    " the arguments for this fragment. Please use the newInstance(...) method for" +
                    " proper instantiation.");
        }
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
        mAdapter = new TagChipsAdapter(selectedTags, this);
        layout.setAdapter(mAdapter);
        return v;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Notify listeners that the tag has been closed.
     * @param chip The chip representing the tag that has been closed.
     */
    @Override
    public void onTagClosed(TagChipView chip) {
        if (mListener != null) {
            mListener.onTagChipClosed(chip.getChipTag());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        mAdapter = null;
        super.onDestroy();
    }

    /**
     * Add a tag to the selected tags.
     * @param tag The tag to be added to the selected tags list.
     */
    public void addTag(ITag tag) {
        mAdapter.add(tag);
    }

    /**
     * Remove Tag from the list of selected tags.
     * @param tag The tag to be removed from selected tags.
     */
    public void removeTag(ITag tag) {
        mAdapter.remove(tag);
    }

    /**
     * @return The active list of selected tags.
     */
    @NonNull
    public List<ITag> getSelectedTags() {
        return mAdapter.getSelectedTags();
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
     * Listener for monitoring the closing of Tags. When a tag is closed, it has been
     * removed from the list of selected tags. This interface should be implemented by any
     * Activity that works with {@link TagChipsFragment}.
     */
    public interface TagChipsFragmentListener {
        /**
         * User removed the Tag from the Tag Chips View by closing it.
         * @param tag The {@link ITag} that has been removed from the
         *            selected list.
         */
        void onTagChipClosed(ITag tag);
    }
}
