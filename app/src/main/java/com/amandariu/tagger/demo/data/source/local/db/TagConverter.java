package com.amandariu.tagger.demo.data.source.local.db;

import android.support.annotation.NonNull;

import com.amandariu.tagger.ITag;
import com.amandariu.tagger.demo.data.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods for converting between {@link ITag} and {@link TagEntity} classes.
 *
 * @author amandariu (11/12/17)
 */
public abstract class TagConverter {
    /**
     * Convert a list of {@link TagEntity} objects which are database-related classes, into
     * a list of {@link ITag} objects which are used in the rest of the application.
     *
     * @param tags A list of {@link TagEntity} objects for conversion.
     * @return The converted list of tags.
     */
    public static List<ITag> toTagList(@NonNull List<TagEntity> tags) {
        final List<ITag> newTags = new ArrayList<>(tags.size());
        for (TagEntity t : tags) {
            newTags.add(new Tag(t.getId(), t.getLabel(), t.getColor()));
        }
        return newTags;
    }

    /**
     * Convert a list of {@link ITag} objects which are used throughout the application,
     * into a list of {@link TagEntity} objects which are specifically used by the database.
     *
     * @param tags A list of {@link ITag} objects for conversion.
     * @return The converted list of tags.
     */
    public static List<TagEntity> toTagEntityList(@NonNull List<? extends ITag> tags) {
        final List<TagEntity> newTags = new ArrayList<>(tags.size());
        for (ITag t : tags) {
            newTags.add(new TagEntity(t));
        }
        return newTags;
    }
}
