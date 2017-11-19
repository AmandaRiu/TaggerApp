package com.amandariu.tagger;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class representation of a Tag.
 * todo do I even need this class anymore?
 *
 * @author amandariu (11/12/17)
 */
public class Tag extends ITag implements Parcelable {

    private int mId;
    private String mLabel;
    private String mColor;

    public Tag(int id, String label, String color) {
        mId = id;
        mLabel = label;
        mColor = color;
    }

    public Tag(ITag otag) {
        mId = otag.getId();
        mLabel = otag.getLabel();
        mColor = otag.getColor();
    }

    protected Tag(Parcel in) {
        mId = in.readInt();
        mLabel = in.readString();
        mColor = in.readString();
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

    public String getLabel() {
        return mLabel;
    }

    public int getColorInt() {
        return TagUtils.getColorInt(mColor);
    }

    public String getColor() {
        return mColor;
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
        dest.writeString(mLabel);
        dest.writeString(mColor);
    }
}
