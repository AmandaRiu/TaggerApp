package com.amandariu.tagger;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.xiaofeng.flowlayoutmanager.Alignment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

/**
 * @author amandariu (11/12/17)
 */
public class TagChipsLayout extends RecyclerView {

    public TagChipsLayout(Context context) {
        super(context, null);
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
    }
}
