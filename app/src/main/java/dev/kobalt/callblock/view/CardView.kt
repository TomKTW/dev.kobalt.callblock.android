package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

/** Wrapped view of CardView. */
open class CardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr)