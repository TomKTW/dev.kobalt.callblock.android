package dev.kobalt.callblock.call

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dev.kobalt.callblock.R
import dev.kobalt.callblock.databinding.CallRecyclerItemBinding
import dev.kobalt.callblock.extension.internationalPhoneNumber
import dev.kobalt.callblock.view.RecyclerView
import java.time.format.DateTimeFormatter

/** Recycler view for listing calls. */
class CallRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun getAdapter(): Adapter = super.getAdapter() as Adapter

    override fun getLayoutManager(): LinearLayoutManager =
        super.getLayoutManager() as LinearLayoutManager

    init {
        adapter = Adapter()
        layoutManager = LinearLayoutManager(context)
    }

    var list: List<CallEntity>
        get() {
            return adapter.list
        }
        set(value) {
            adapter.list = value
        }

    var onItemSelect: ((CallEntity) -> Unit)?
        get() {
            return adapter.onItemSelect
        }
        set(value) {
            adapter.onItemSelect = value
        }

    class PhoneNumberDiff : DiffUtil.ItemCallback<CallEntity>() {
        override fun areItemsTheSame(
            oldItem: CallEntity,
            newItem: CallEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CallEntity,
            newItem: CallEntity
        ): Boolean = oldItem == newItem
    }

    class Adapter(
        var onItemSelect: ((CallEntity) -> Unit)? = null
    ) : RecyclerView.Adapter<Holder<*>>() {

        private val differ: AsyncListDiffer<CallEntity> =
            AsyncListDiffer(this, PhoneNumberDiff())

        var list: List<CallEntity>
            get() = differ.currentList
            set(value) = differ.submitList(value)

        override fun getItemViewType(position: Int): Int {
            return when (list.getOrNull(position)) {
                is CallEntity -> 0
                else -> throw Exception("Invalid item type.")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<*> {
            when (viewType) {
                0 -> return ItemHolder(
                    CallRecyclerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ).apply {
                    binding.root.setOnClickListener {
                        list.getOrNull(adapterPosition)?.let {
                            onItemSelect?.invoke(it)
                        }
                    }
                }
                else -> throw Exception("Invalid view type.")
            }
        }

        override fun onBindViewHolder(holder: Holder<*>, position: Int) {
            when (holder) {
                is ItemHolder -> {
                    list.getOrNull(holder.adapterPosition).let { item ->
                        holder.binding.apply {
                            titleLabel.text =
                                item?.number?.let { root.context.internationalPhoneNumber(it) }
                            subtitleLabel.text = when (item?.action) {
                                CallEntity.Action.Allow -> root.context.getString(R.string.calls_item_action_allow_value)
                                CallEntity.Action.Warn -> root.context.getString(R.string.calls_item_action_warn_value)
                                CallEntity.Action.Block -> root.context.getString(R.string.calls_item_action_block_value)
                                // If value is by any chance anything else, treat it as undetermined.
                                null -> root.context.getString(R.string.rule_item_action_undetermined_value)
                            }
                            subsubtitleLabel.text =
                                item?.timestamp?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    open class Holder<T : ViewBinding>(binding: T) : RecyclerView.BindingHolder<T>(binding)

    class ItemHolder(binding: CallRecyclerItemBinding) : Holder<CallRecyclerItemBinding>(binding)

}