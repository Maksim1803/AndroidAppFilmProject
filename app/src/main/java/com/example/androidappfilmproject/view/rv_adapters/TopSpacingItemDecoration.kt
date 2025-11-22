package com.example.androidappfilmproject.view.rv_adapters

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// Создаем класс TopSpacingItemDecoration для добавления отступов к элементам RecyclerView.
class TopSpacingItemDecoration (private val paddingInDp: Int): RecyclerView.ItemDecoration() {
    // Расширение для конвертации Dp в пиксели.
    private val Int.convertPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    // Метод для установки отступов для каждого элемента списка.
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        // Устанавливаем верхний, правый и левый отступы в пикселях.
        outRect.top = paddingInDp.convertPx
        outRect.right = paddingInDp.convertPx
        outRect.left = paddingInDp.convertPx
    }
}