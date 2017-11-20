package com.amandariu.tagger.demo.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.amandariu.tagger.ITag;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Class representation of a tag element.
 *
 * Extends {@link ITag} so it can easily be used by the custom tag selector module.
 * Is also a LoganSquare JSON object for easy translation from the JSON received from
 * the network to a POJO.
 *
 * @author Amanda Riu
 */
@JsonObject
public class Tag extends ITag implements Parcelable {

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

    public Tag(int id, String label, String color) {
        this.mId = id;
        this.mLabel = label;
        this.mColor = color;
    }

    public Tag(Parcel in) {
        mId = in.readInt();
        mLabel = in.readString();
        mColor = in.readString();
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public String getLabel() {
        return mLabel;
    }

    @Override
    public String getColor() {
        return mColor;
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
        dest.writeString(mLabel);
        dest.writeString(mColor);
    }
}
