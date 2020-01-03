package com.giza.gizaamrdata.utils.extensions

import androidx.recyclerview.widget.RecyclerView

/**
 * @author hossam.
 */

internal inline fun RecyclerView.onScroll(crossinline body: (dx: Int, dy: Int) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            body(dx, dy)
        }
    })
}