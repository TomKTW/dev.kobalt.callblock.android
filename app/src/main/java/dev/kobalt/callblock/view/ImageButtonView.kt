package dev.kobalt.callblock.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dev.kobalt.callblock.R
import dev.kobalt.callblock.extension.dp

/** Wrapped view of ImageButton. */
open class ImageButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    init {
        onInit(attrs, defStyleAttr)
    }

    // Internal values as alternative to native variants.
    private var _backgroundTint: Int = Color.BLACK
    private var _rippleTint: Int = Color.BLACK
    private var _rippleInset: Int = 0

    /** Drawable value of image to show. */
    var image: Drawable?
        get() = drawable
        set(value) {
            setImageDrawable(value)
        }

    /** Color value for background tint. */
    var backgroundTint: Int
        get() = _backgroundTint
        set(value) {
            _backgroundTint = value
            updateBackground()
        }

    /** Color value for image tint. */
    var imageTint: Int?
        get() = imageTintList?.defaultColor
        set(value) {
            imageTintList = value?.let { ColorStateList.valueOf(value) }
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
        // Apply values from XML layouts.
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ImageButtonView,
            defStyleAttr,
            0
        ).let {
            try {
                backgroundTint =
                    it.getColor(R.styleable.ImageButtonView_backgroundTintColor, Color.TRANSPARENT)
                rippleTint = it.getColor(R.styleable.ImageButtonView_rippleTintColor, Color.BLACK)
                imageTint = it.getColor(R.styleable.ImageButtonView_imageTintColor, Color.BLACK)
            } finally {
                it.recycle()
            }
        }
        rippleInset = context.dp(4)
    }

    /** Update background after value has been changed. */
    private fun updateBackground() {
        // Background consists of oval drawable with background and ripple tint colors.
        background = (LayerDrawable(
            arrayOf(
                ShapeDrawable(OvalShape()).apply { paint.color = backgroundTint },
                RippleDrawable(
                    ColorStateList.valueOf(rippleTint), null, ShapeDrawable(OvalShape())
                ).apply {
                    rippleInset.let { setLayerInset(0, it, it, it, it) }
                }
            )
        ))
    }

}