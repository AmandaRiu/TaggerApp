package com.amandariu.tagger.demo.data.source.local.db

import com.amandariu.tagger.ITag
import com.amandariu.tagger.demo.data.Tag

import java.util.ArrayList

/**
 * Methods for converting between [ITag] and [TagEntity] classes.
 *
 * @author Amanda Riu
 */
object TagConverter {
    /**
     * Convert a list of [TagEntity] objects which are database-related classes, into
     * a list of [ITag] objects which are used in the rest of the application.
     *
     * @param tags A list of [TagEntity] objects for conversion.
     * @return The converted list of tags.
     */
    fun toTagList(tags: List<TagEntity>): List<ITag> {
        val newTags = ArrayList<ITag>(tags.size)
        for (t in tags) {
            newTags.add(Tag(t.id, t.label!!, t.color!!))
        }
        return newTags
    }

    /**
     * Convert a list of [ITag] objects which are used throughout the application,
     * into a list of [TagEntity] objects which are specifically used by the database.
     *
     * @param tags A list of [ITag] objects for conversion.
     * @return The converted list of tags.
     */
    fun toTagEntityList(tags: List<ITag>): List<TagEntity> {
        val newTags = ArrayList<TagEntity>(tags.size)
        for (t in tags) {
            newTags.add(TagEntity(t))
        }
        return newTags
    }
}
