package com.amandariu.tagger

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

import com.xiaofeng.flowlayoutmanager.Alignment
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager

/**
 * Layout container for displaying [TagChipView] objects. This layout uses
 * the FlowLayoutManager (https://github.com/xiaofeng-han/AndroidLibs/tree/master/flowlayoutmanager)
 * to automatically shift items to the next line when there is not enough room to add the item
 * to the current line.
 *
 * @author Amanda Riu
 */
class TagChipsLayout @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    init {
        val flowMgr = FlowLayoutManager()
        flowMgr.isAutoMeasureEnabled = true
        flowMgr.setAlignment(Alignment.LEFT)
        flowMgr.singleItemPerLine()
        layoutManager = flowMgr
        //
        // Add 8 pixels of spacing between tag chips.
        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(4, 4, 4, 4)
            }
        })
    }
}
