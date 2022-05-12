package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/** Wrapped view of FrameLayout. */
open class FrameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)