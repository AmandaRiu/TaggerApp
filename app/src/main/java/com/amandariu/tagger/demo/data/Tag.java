package com.amandariu.tagger.demo.data;


import com.amandariu.tagger.ITag;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Class representation of a tag element.
 *
 * @author Amanda Riu
 */
@JsonObject
public class Tag implements ITag {

    @JsonField(name = "id")
    int mId;

    @JsonField(name = "color")
    String mColor;

    @JsonField(name = "tag")
    String mLabel;

    transient boolean mUserAdded = false;

    public Tag() {
        // empty constructor required by LoganSquare
    }

    public Tag(ITag tag) {
        this.mId = tag.getId();
        this.mColor = tag.getColor();
        this.mLabel = tag.getLabel();
    }

    public Tag(int id, String color, String label) {
        this.mId = id;
        this.mColor = color;
        this.mLabel = label;
    }

    public Tag(int id, String color, String label, boolean userAdded) {
        this(id, color, label);
        this.mUserAdded = userAdded;
    }

    public int getId() {
        return mId;
    }

    public String getColor() {
        return mColor;
    }

    public String getLabel() {
        return mLabel;
    }
}
