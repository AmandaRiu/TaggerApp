package com.amandariu.tagger;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.xiaofeng.flowlayoutmanager.Alignment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

/**
 * Layout container for displaying {@link TagChipView} objects. This layout uses
 * the FlowLayoutManager (https://github.com/xiaofeng-han/AndroidLibs/tree/master/flowlayoutmanager)
 * to automatically shift items to the next line when there is not enough room to add the item
 * to the current line.
 *
 * @author Amanda Riu
 */
public class TagChipsLayout extends RecyclerView {

    public TagChipsLayout(Context context) {
        this(context, null);
    }

    public TagChipsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagChipsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        FlowLayoutManager flowMgr = new FlowLayoutManager();
        flowMgr.setAutoMeasureEnabled(true);
        flowMgr.setAlignment(Alignment.LEFT);
        flowMgr.singleItemPerLine();
        setLayoutManager(flowMgr);
        //
        // Add 8 pixels of spacing between tag chips.
        addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(4, 4, 4, 4);
            }
        });
    }
}
