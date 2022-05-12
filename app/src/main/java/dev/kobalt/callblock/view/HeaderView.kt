package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.setPadding
import dev.kobalt.callblock.R
import dev.kobalt.callblock.extension.dp
import dev.kobalt.callblock.extension.getResourceColor
import dev.kobalt.callblock.extension.getResourceDrawable
import dev.kobalt.callblock.extension.sp

/** View of header containing title and if needed, action buttons. */
open class HeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalStackView(context, attrs, defStyleAttr) {

    private val backButton = AppCompatImageButton(context).apply {
        setImageDrawable(context.getResourceDrawable(R.drawable.ic_baseline_arrow_back_24))
        setPadding(context.dp(16))
    }

    private val titleLabel = LabelView(context).apply {
        text = context.getString(R.string.app_name)
        setTextColor(context.getResourceColor(R.color.white))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.sp(20).toFloat())
    }

    private val iconImage = ImageView(context).apply {
        setImageDrawable(context.getResourceDrawable(R.drawable.ic_baseline_phone_cancel_24))
        setPadding(context.dp(16))
    }

    init {
        onInit()
    }

    fun onInit() {
        gravity = Gravity.CENTER
        context.apply {
            setBackgroundColor(getResourceColor(R.color.red_800))
            addView(iconImage, LayoutParams(dp(56), dp(56)))
            addView(titleLabel, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f))
        }
    }

}