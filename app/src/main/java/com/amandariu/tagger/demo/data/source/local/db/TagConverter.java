package com.amandariu.tagger.demo.data.source.local.db;

import android.support.annotation.NonNull;

import com.amandariu.tagger.demo.data.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * todo best way to do this?
 *
 * @author amandariu (11/12/17)
 */
public abstract class TagConverter {

    public static List<Tag> toTagList(@NonNull List<TagEntity> tags) {
        final List<Tag> newTags = new ArrayList<>(tags.size());
        for (TagEntity t : tags) {
            newTags.add(new Tag(t));
        }
        return newTags;
    }

    public static List<TagEntity> toTagEntityList(@NonNull List<Tag> tags) {
        final List<TagEntity> newTags = new ArrayList<>(tags.size());
        for (Tag t : tags) {
            newTags.add(new TagEntity(t));
        }
        return newTags;
    }
}
