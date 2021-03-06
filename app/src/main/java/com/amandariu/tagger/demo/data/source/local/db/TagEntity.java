package com.amandariu.tagger.demo.data.source.local.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import com.amandariu.tagger.ITag;

/**
 * Represents a single table row in the tags database.
 *
 * @author amandariu (11/12/17)
 */
@Entity(tableName = "tags")
public class TagEntity {

    @PrimaryKey
    private int id;

    private String label;
    private String color;

    public TagEntity() {
    }

    public TagEntity(ITag tag) {
        this.id = tag.getId();
        this.label = tag.getLabel();
        this.color = tag.getColor();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
