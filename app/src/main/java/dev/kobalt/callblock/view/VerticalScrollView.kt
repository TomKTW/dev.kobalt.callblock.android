package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/** Wrapper view of NestedScrollView. Nested variant is used as normal ScrollView cannot be used for nested views. */
class VerticalScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr)