package dev.kobalt.callblock.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import com.lamudi.phonefield.PhoneEditText
import dev.kobalt.callblock.R
import dev.kobalt.callblock.extension.dp
import dev.kobalt.callblock.extension.getResourceColor
import java.util.*

open class PhoneInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PhoneEditText(context, attrs, defStyleAttr) {

    init {
        onInit()
    }

    private fun onInit() {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.background = null
        editText.setTextColor(context.getResourceColor(R.color.black))
        editText.setHintTextColor(context.getResourceColor(R.color.black_a50))
        editText.setPadding(0)
        spinner.background = null
        spinner.updatePadding(0, 0, context.dp(8), 0)
        spinner.updateLayoutParams<MarginLayoutParams> {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            updateMargins(0, 0, 0, 0)
        }
        editText.hint = "Phone number"
        setDefaultCountry(Locale.getDefault().country)
    }

    /*
     * Possible workaround method overrides for crash that seems to occur on Samsung devices on long tap.
     *
     * References:
     * https://stackoverflow.com/questions/42926522/java-lang-nullpointerexception-with-nougat
     * https://stackoverflow.com/questions/52497289/app-crashes-when-long-clicking-on-text-view-hint
     *
     * Cause (WTF?):
     * "Attempt to invoke virtual method 'boolean android.widget.Editor$SelectionModifierCursorController.isDragAcceleratorActive()' on a null object reference"
     */

    override fun performLongClick(): Boolean {
        return try {
            super.performLongClick()
        } catch (e: NullPointerException) {
            e.printStackTrace()
            true
        }
    }

    override fun performLongClick(x: Float, y: Float): Boolean {
        return try {
            super.performLongClick(x, y)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return try {
            super.onTouchEvent(event)
        } catch (e: NullPointerException) {
            e.printStackTrace(); true
        }
    }

    override fun performClick(): Boolean {
        return try {
            super.performClick()
        } catch (e: NullPointerException) {
            e.printStackTrace()
            true
        }
    }

}