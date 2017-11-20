package com.amandariu.tagger;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Custom Chip view. This view contains a label, and optionally, a 'delete' button.
 *
 * @author amandariu (11/12/17)
 */
public class TagChipView extends LinearLayout {

    private static final String TAG = TagChipView.class.getSimpleName();

    interface TagChipListener {
        void onTagClosed(TagChipView chip);
    }


    private ITag mTag;
    private final TextView mTxtLabel;
    private final ImageView mBtnDelete;

    private TagChipListener mListener = null;

    public TagChipView(Context context) {
        this(context, null);
    }

    public TagChipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagChipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_tag_chip, this, true);
        mTxtLabel = findViewById(R.id.txt_label);
        mBtnDelete = findViewById(R.id.btn_delete);

        final TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.TagChipView, 0, 0);
        try {
            String label = a.getString(R.styleable.TagChipView_tagChipLabel);
            if (label != null) {
                mTxtLabel.setText(label);
            }
            int labelColor = a.getColor(R.styleable.TagChipView_tagChipLabelColor, 0xffffffff);
            mTxtLabel.setTextColor(labelColor);
            boolean allowDelete = a.getBoolean(R.styleable.TagChipView_tagChipAllowClose, true);
            mBtnDelete.setVisibility(allowDelete ? VISIBLE : GONE);
        } finally {
            a.recycle();
        }

        if (mBtnDelete.getVisibility() == VISIBLE) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onTagClosed(TagChipView.this);
                    }
                }
            });
        }
    }


    public void setChipTag(ITag tag) {
        mTag = tag;
        mTxtLabel.setText(mTag.getLabel());
        if (mTag.getColorInt() != -1) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(mTag.getColorInt());
            gd.setCornerRadius(TagUtils.getDensityPixel(getContext(), 16));
            setBackground(gd);
        }
        invalidate();
        requestLayout();
    }

    @Nullable
    public ITag getChipTag() {
        return mTag;
    }


    public void setOnTagDeletedListener(TagChipListener listener) {
        mListener = listener;
    }

}
