package com.amandariu.tagger;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 */
public class TagChipsFragment extends Fragment implements TagChipView.TagChipListener {

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
     * @return A new instance of fragment TagChipsFragment.
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
            mListener.onTagChipClosed(chip.getChipTag());
        }
    }

    @Override
    public void onDestroy() {
        mAdapter = null;

        super.onDestroy();
    }

    public void addTag(ITag tag) {
        mAdapter.add(mAdapter.getItemCount(), tag);
    }

    public void removeTag(ITag tag) {
        mAdapter.remove(tag);
    }

    public List<ITag> getSelectedTags() {
        return mAdapter.getSelectedTags();
    }

    public interface TagChipsFragmentListener {
        /**
         * User removed the Tag from the Tag Chips View by closing it.
         * @param tag The {@link ITag} that has been removed from the
         *            selected list.
         */
        void onTagChipClosed(ITag tag);
    }
}
