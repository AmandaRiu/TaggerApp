package com.amandariu.tagger.demo.data

import android.os.Parcel
import android.os.Parcelable

import com.amandariu.tagger.ITag
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

/**
 * Class representation of a tag element.
 *
 * Extends [ITag] so it can easily be used by the custom tag selector module.
 * Is also a LoganSquare JSON object for easy translation from the JSON received from
 * the network to a POJO.
 *
 * @author Amanda Riu
 */
@JsonObject
class Tag : ITag, Parcelable {

    @JsonField(name = arrayOf("id"))
    override var id: Int = 0

    @JsonField(name = arrayOf("color"))
    override var color: String = ""

    @JsonField(name = arrayOf("tag"))
    override var label: String = ""


    constructor() {
        // empty constructor required by LoganSquare
    }

    constructor(id: Int, label: String, color: String) {
        this.id = id
        this.label = label
        this.color = color
    }

    private constructor(`in`: Parcel) {
        id = `in`.readInt()
        label = `in`.readString()
        color = `in`.readString()
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of [.writeToParcel],
     * the return value of this method must include the
     * [.CONTENTS_FILE_DESCRIPTOR] bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or [.PARCELABLE_WRITE_RETURN_VALUE].
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(label)
        dest.writeString(color)
    }

    companion object {
        @Suppress("UNUSED")
        @JvmField
        val CREATOR: Parcelable.Creator<Tag> = object : Parcelable.Creator<Tag> {
            override fun createFromParcel(`in`: Parcel): Tag {
                return Tag(`in`)
            }

            override fun newArray(size: Int): Array<Tag?> {
                return arrayOfNulls(size)
            }
        }
    }
}
