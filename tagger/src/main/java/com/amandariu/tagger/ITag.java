package com.amandariu.tagger;


import android.os.Parcelable;

/**
 * @author amandariu (11/12/17)
 */
public abstract class ITag implements Parcelable {

    abstract public int getId();
    abstract public String getLabel();
    abstract public String getColor();

    public int getColorInt() {
        return TagUtils.getColorInt(getColor());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ITag)) return false;
        if (((ITag) obj).getId() == getId()) {
            return true;
        }
        return false;
    }
}
