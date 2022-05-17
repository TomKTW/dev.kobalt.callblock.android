package dev.kobalt.callblock.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import dev.kobalt.callblock.R

/** Wrapped view of Button. */
open class LabelButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        onInit(attrs, defStyleAttr)
    }

    private var _backgroundTint: Int = Color.BLACK
    private var _rippleTint: Int = Color.BLACK
    private var _topImage: Drawable? = null
    private var _topImageTint: Int = Color.BLACK

    /** Drawable value of image above text label to show. */
    var topImage: Drawable?
        get() = _topImage
        set(value) {
            _topImage = value
            updateTopImage()
        }

    /** Color value for tint of image above text label. */
    var topImageTint: Int
        get() = _topImageTint
        set(value) {
            _topImageTint = value
            updateTopImage()
        }

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

    @Suppress("UNUSED_PARAMETER")
    private fun onInit(attrs: AttributeSet?, defStyleAttr: Int) {
        // Apply values from XML layouts.
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LabelButtonView,
            defStyleAttr,
            0
        ).let {
            try {
                backgroundTint =
                    it.getColor(R.styleable.LabelButtonView_backgroundTintColor, Color.TRANSPARENT)
                rippleTint = it.getColor(R.styleable.LabelButtonView_rippleTintColor, Color.BLACK)
                topImage = it.getDrawable(R.styleable.LabelButtonView_topImage)
                topImageTint = it.getColor(R.styleable.LabelButtonView_imageTintColor, Color.BLACK)
            } finally {
                it.recycle()
            }
        }
    }

    /** Update background after value has been changed. */
    private fun updateBackground() {
        // Background consists of drawable with background and ripple tint colors.
        background = (LayerDrawable(
            arrayOf(
                ShapeDrawable(RectShape()).apply { paint.color = backgroundTint },
                RippleDrawable(
                    ColorStateList.valueOf(rippleTint), null, ShapeDrawable(RectShape())
                )
            )
        ))
    }

    /** Update top image after value has been changed. */
    private fun updateTopImage() {
        // Use native compound drawable on top to apply the drawable and apply tint to it manually.
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, topImage?.mutate()?.also {
            it.setTintMode(PorterDuff.Mode.SRC_IN)
            it.setTint(topImageTint)
        }, null, null)
    }
}