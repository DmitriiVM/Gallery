package com.example.gallery

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreCachingLayoutManager(
    private val context: Context,
    spanCount: Int,
    orientation: Int,
    reverseLayout: Boolean
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int =
        context.resources.displayMetrics.heightPixels * 2

}