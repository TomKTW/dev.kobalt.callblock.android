package dev.kobalt.callblock.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/** Wrapped view of Button. */
open class ButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        onInit(attrs, defStyleAttr)
    }

    private var _backgroundTint: Int = Color.BLACK
    private var _rippleTint: Int = Color.BLACK
    private var _rippleInset: Int = 0

    /** Color value for background tint. */
    var backgroundTint: Int
        get() = _backgroundTint
        set(value) {
            _backgroundTint = value
            updateBackground()
        }

    /** Color value for ripple tint. */
    var rippleTint: Int
        get() = _rippleTint
        set(value) {
            _rippleTint = value
            updateBackground()
        }

    /** Size value for ripple offset. */
    var rippleInset: Int
        get() = _rippleInset
        set(value) {
            _rippleInset = value
            updateBackground()
        }

    @Suppress("UNUSED_PARAMETER")
    private fun onInit(attrs: AttributeSet?, defStyleAttr: Int) {
        rippleTint = Color.BLACK
        rippleInset = 0
    }

    /** Update background after value has been changed. */
    private fun updateBackground() {
        background = (LayerDrawable(
            arrayOf(
                ShapeDrawable(RectShape()).apply { paint.color = backgroundTint },
                RippleDrawable(
                    ColorStateList.valueOf(rippleTint), null, ShapeDrawable(RectShape())
                ).apply {
                    rippleInset.let { setLayerInset(0, it, it, it, it) }
                }
            )
        ))
    }

}