package dev.kobalt.callblock.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/** Wrapped view of RecyclerView. */
open class RecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    abstract class Adapter<T : Holder> : RecyclerView.Adapter<T>()

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    abstract class BindingHolder<T : ViewBinding>(val binding: T) : Holder(binding.root)

}