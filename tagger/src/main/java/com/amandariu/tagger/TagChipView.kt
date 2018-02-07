package com.amandariu.tagger

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


/**
 * Custom Chip view. This view contains a label, and optionally, a 'delete' button.
 *
 * @author Amanda Riu
 */
class TagChipView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    var chipTag: ITag? = null
        set(tag) {
            field = tag
            mTxtLabel.text = chipTag?.label

            chipTag?.let {
                val gd = GradientDrawable()
                gd.setColor(chipTag!!.colorInt)
                gd.cornerRadius = getDensityPixel(context, 16).toFloat()
                background = gd
                invalidate()
                requestLayout()
            }
        }

    private val mTxtLabel: TextView
    private val mBtnDelete: ImageView
    private var mListener: TagChipListener? = null

    interface TagChipListener {
        /**
         * User has clicked the chip to remove it from the selected
         * tags list.
         */
        fun onTagClosed(chip: TagChipView)
    }

    init {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_tag_chip, this, true)
        mTxtLabel = findViewById(R.id.txt_label)
        mBtnDelete = findViewById(R.id.btn_delete)

        val a = context.theme
                .obtainStyledAttributes(attrs, R.styleable.TagChipView, 0, 0)
        try {
            val label = a.getString(R.styleable.TagChipView_tagChipLabel)
            if (label != null) {
                mTxtLabel.text = label
            }
            val labelColor = a.getColor(R.styleable.TagChipView_tagChipLabelColor, -0x1)
            mTxtLabel.setTextColor(labelColor)
            val allowDelete = a.getBoolean(R.styleable.TagChipView_tagChipAllowClose, true)
            mBtnDelete.visibility = if (allowDelete) View.VISIBLE else View.GONE
        } finally {
            a.recycle()
        }

        if (mBtnDelete.visibility == View.VISIBLE) {
            setOnClickListener {
                mListener?.onTagClosed(this@TagChipView)
            }
        }
    }

    fun setOnTagDeletedListener(listener: TagChipListener) {
        mListener = listener
    }
}
