package com.amandariu.tagger.demo.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.amandariu.tagger.ITag;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Class representation of a tag element.
 *
 * @author Amanda Riu
 */
@JsonObject
public class Tag implements ITag, Parcelable {

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

    protected Tag(Parcel in) {
        mId = in.readInt();
        mColor = in.readString();
        mLabel = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getColor() {
        return mColor;
    }

    public String getLabel() {
        return mLabel;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mColor);
        dest.writeString(mLabel);
    }
}
