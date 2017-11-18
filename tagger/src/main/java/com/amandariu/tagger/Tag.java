package com.amandariu.tagger;

import com.amandariu.tagger.common.ColorConverter;

/**
 *
 *
 * @author amandariu (11/12/17)
 */
public class Tag {

    private int mId;
    private String mLabel;
    private int mColorInt;

    public Tag(int id, String label, int colorInt) {
        this.mId = id;
        this.mLabel = label;
        this.mColorInt = colorInt;
    }

    public Tag(ITag otag) {
        mId = otag.getId();
        mLabel = otag.getLabel();
        mColorInt = ColorConverter.getColorInt(otag.getColor());
    }

    public int getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    public int getColorInt() {
        return mColorInt;
    }
}
