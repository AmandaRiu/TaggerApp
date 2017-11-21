package com.amandariu.tagger.demo.data.source.local.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.amandariu.tagger.ITag

/**
 * Represents a single table row in the tags database.
 *
 * @author Amanda Riu
 */
@Entity(tableName = "tags")
class TagEntity {

    @PrimaryKey
    var id: Int = 0

    var label: String? = null
    var color: String? = null

    constructor()

    constructor(tag: ITag) {
        this.id = tag.id
        this.label = tag.label
        this.color = tag.color
    }
}
