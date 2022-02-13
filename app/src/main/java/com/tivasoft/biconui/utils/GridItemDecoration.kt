package com.tivasoft.biconui.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Generic class for setting item space in recyclerview
 *
 * @param space value converted into pixel
 * @param isBackpack whether or not the spacing is for backpack
 */
class GridItemDecoration(
    private val space: Int,
    private val isBackpack: Boolean = false
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space / 2
        outRect.right = space / 2
        outRect.bottom = when (isBackpack && parent.getChildLayoutPosition(view) > 1) {
            true -> 0
            false -> space
        }
        outRect.top = when (parent.getChildLayoutPosition(view)) {
            0, 1 -> space / 2
            else -> 0
        }
    }
}