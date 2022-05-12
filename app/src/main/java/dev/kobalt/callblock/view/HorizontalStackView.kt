package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat

/** Wrapped view of LinearLayout with horizontal orientation. */
open class HorizontalStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL
    }

}