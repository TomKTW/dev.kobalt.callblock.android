package dev.kobalt.callblock.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.core.view.isVisible
import dev.kobalt.callblock.R
import dev.kobalt.callblock.extension.dp
import dev.kobalt.callblock.extension.getResourceColor
import dev.kobalt.callblock.extension.sp

/** View used for describing an option with title and subtitle, along with an optional switch. */
open class OptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalStackView(context, attrs, defStyleAttr) {

    val titleLabel = LabelView(context).apply {
        setTextColor(context.getResourceColor(R.color.black))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.sp(16).toFloat())
    }

    val subtitleLabel = LabelView(context).apply {
        setTextColor(context.getResourceColor(R.color.black))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.sp(14).toFloat())
        alpha = 0.75f
    }

    val optionSwitch = SwitchView(context).apply {
        isClickable = false
    }

    val optionButton = LabelButtonView(context).apply {
        isClickable = false
        setSupportAllCaps(true)
        background = null
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        // Update enabled state for subviews.
        titleLabel.alpha = if (enabled) 1.0f else 0.5f
        subtitleLabel.alpha = if (enabled) 0.75f else 0.25f
        optionSwitch.alpha = if (enabled) 1.0f else 0.5f
        titleLabel.isEnabled = enabled
        subtitleLabel.isEnabled = enabled
        optionSwitch.isEnabled = enabled
    }

    init {
        onInit(context, attrs, defStyleAttr)
    }

    private fun onInit(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Position all views to be centered.
        gravity = Gravity.CENTER
        // Apply values from XML layouts.
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.OptionView,
            defStyleAttr,
            0
        ).let {
            try {
                titleLabel.text = it.getString(R.styleable.OptionView_titleLabel)
                subtitleLabel.text = it.getString(R.styleable.OptionView_subtitleLabel)
                optionSwitch.isVisible = it.getBoolean(R.styleable.OptionView_visibleSwitch, false)
                optionButton.isVisible = it.getBoolean(R.styleable.OptionView_visibleButton, false)
                optionButton.text = it.getString(R.styleable.OptionView_buttonLabelText)
            } finally {
                it.recycle()
            }
        }
        // Label stack contains title and subtitle labels in vertical orientation.
        val labelStack = VerticalStackView(context).apply {
            addView(titleLabel)
            addView(subtitleLabel)
            gravity = Gravity.CENTER
        }
        // Add all views.
        addView(labelStack, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f))
        addView(SpaceView(context), LayoutParams(context.dp(4), LayoutParams.MATCH_PARENT))
        addView(optionSwitch)
        addView(optionButton)
        // Apply ripple to background.
        background = RippleDrawable(
            ColorStateList.valueOf(context.getResourceColor(R.color.primary_normal)),
            null,
            ShapeDrawable(RectShape())
        )
    }

}