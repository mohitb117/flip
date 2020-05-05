package com.mohitb117.flip

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * A special decorator to allow adding a defined separation between items.
 */
class VerticalSpaceItemDecoration(val height: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = height
    }
}