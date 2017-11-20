package com.amandariu.tagger;


import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Interface for working with individual Tag elements.
 * @author Amanda Riu
 */
public abstract class ITag implements Parcelable, Comparable {

    abstract public int getId();
    abstract public String getLabel();
    abstract public String getColor();

    /**
     * @return The integer parsed from the color string.
     */
    public int getColorInt() {
        return TagUtils.getColorInt(getColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ITag)) return false;
        return ((ITag) obj).getId() == getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@NonNull Object o) {
        return getLabel().toLowerCase().compareTo(((ITag)o).getLabel().toLowerCase());
    }
}
