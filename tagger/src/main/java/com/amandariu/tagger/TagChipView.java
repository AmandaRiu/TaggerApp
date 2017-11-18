package com.amandariu.tagger;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amandariu.tagger.common.AndroidUtils;


/**
 * Custom Chip view. This view contains a label, and optionally, a 'delete' button.
 *
 * @author amandariu (11/12/17)
 */
public class TagChipView extends LinearLayout {

    private static final String TAG = TagChipView.class.getSimpleName();

    interface OnTagDeletedListener {
        void onTagDeleted(TagChipView chip);
    }

    private Tag mTag;
    private TextView mTxtLabel;
    private ImageView mBtnDelete;

    private OnTagDeletedListener mListener = null;

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
            String label = a.getString(R.styleable.TagChipView_chipLabel);
            if (label != null) {
                mTxtLabel.setText(label);
            }
            boolean allowDelete = a.getBoolean(R.styleable.TagChipView_allowDelete, true);
            mBtnDelete.setVisibility(allowDelete ? VISIBLE : GONE);
            int color = a.getColor(R.styleable.TagChipView_chipColor, 0xff000000);
            setChipTag(new Tag(0, label, color));
        } finally {
            a.recycle();
        }

        if (mBtnDelete.getVisibility() == VISIBLE) {
            mBtnDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("AMANDA-TEST", "onClick: Delete clicked for view: " + v);
                    if (mListener != null) {
                        mListener.onTagDeleted(TagChipView.this);
                    }
                }
            });
        }
    }


    public void setChipTag(ITag otag) {
        setChipTag(new Tag(otag));
    }


    public void setChipTag(Tag tag) {
        mTag = tag;
        mTxtLabel.setText(mTag.getLabel());
        if (mTag.getColorInt() != -1) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(mTag.getColorInt());
            gd.setCornerRadius(AndroidUtils.getDensityPixel(getContext(), 16));
            setBackground(gd);
        }
        invalidate();
        requestLayout();
    }

    @Nullable
    public Tag getChipTag() {
        return mTag;
    }


    public void setOnTagDeletedListener(OnTagDeletedListener listener) {
        mListener = listener;
    }

}