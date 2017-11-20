package com.amandariu.tagger

import android.os.Parcelable

/**
 * Interface for working with individual Tag elements.
 * @author Amanda Riu
 */
abstract class ITag : Parcelable, Comparable<ITag> {

    abstract val id: Int
    abstract val label: String
    abstract val color: String

    /**
     * @return The integer parsed from the color string.
     */
    val colorInt: Int
        get() = getColorInt(color)

    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        return if (other !is ITag) false else other.id == id
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ITag): Int {
        return label.toLowerCase().compareTo(other.label.toLowerCase())
    }

    /**
     * Returns a hash code value for the object.  The general contract of hashCode is:
     *
     * * Whenever it is invoked on the same object more than once, the hashCode method must
     * consistently return the same integer, provided no information used in equals comparisons
     * on the object is modified.
     * * If two objects are equal according to the equals() method, then calling the hashCode
     * method on each of the two objects must produce the same integer result.
     */
    override fun hashCode(): Int {
        val result = 1
        val c = id
        return 38 * result + c
    }
}