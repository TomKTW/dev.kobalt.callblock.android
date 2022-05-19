package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import io.github.florent37.shapeofview.shapes.RoundRectView

/** Wrapped view of RoundRectView. */
open class RoundRectangleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundRectView(context, attrs, defStyleAttr)