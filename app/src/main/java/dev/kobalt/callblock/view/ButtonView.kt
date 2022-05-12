package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/** Wrapped view of Button. */
open class ButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr)