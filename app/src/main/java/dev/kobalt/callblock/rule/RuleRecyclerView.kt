package dev.kobalt.callblock.rule

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dev.kobalt.callblock.R
import dev.kobalt.callblock.databinding.RuleRecyclerItemBinding
import dev.kobalt.callblock.extension.toStringFormat
import dev.kobalt.callblock.view.RecyclerView
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

/** Recycler view for listing rules. */
class RuleRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun getAdapter(): Adapter = super.getAdapter() as Adapter

    override fun getLayoutManager(): LinearLayoutManager =
        super.getLayoutManager() as LinearLayoutManager

    init {
        adapter = Adapter()
        layoutManager = LinearLayoutManager(context)
    }

    var list: List<RuleEntity>
        get() {
            return adapter.list
        }
        set(value) {
            adapter.list = value
        }

    var onItemSelect: ((RuleEntity) -> Unit)?
        get() {
            return adapter.onItemSelect
        }
        set(value) {
            adapter.onItemSelect = value
        }

    class PhoneNumberDiff : DiffUtil.ItemCallback<RuleEntity>() {
        override fun areItemsTheSame(
            oldItem: RuleEntity,
            newItem: RuleEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: RuleEntity,
            newItem: RuleEntity
        ): Boolean = oldItem == newItem
    }

    class Adapter(
        var onItemSelect: ((RuleEntity) -> Unit)? = null
    ) : RecyclerView.Adapter<Holder<*>>() {

        private val differ: AsyncListDiffer<RuleEntity> =
            AsyncListDiffer(this, PhoneNumberDiff())

        var list: List<RuleEntity>
            get() = differ.currentList
            set(value) = differ.submitList(value)

        override fun getItemViewType(position: Int): Int {
            return when (list.getOrNull(position)) {
                is RuleEntity -> 0
                else -> throw Exception("Invalid item type.")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<*> {
            when (viewType) {
                0 -> return ItemHolder(
                    RuleRecyclerItemBinding.inflate(
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
                                item?.number?.toStringFormat(PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                            subtitleLabel.text = when (item?.action) {
                                RuleEntity.Action.Allow -> root.context.getString(R.string.rule_item_action_allow_value)
                                RuleEntity.Action.Warn -> root.context.getString(R.string.rule_item_action_warn_value)
                                RuleEntity.Action.Block -> root.context.getString(R.string.rule_item_action_block_value)
                                // If value is by any chance anything else, treat it as undetermined.
                                null -> root.context.getString(R.string.rule_item_action_undetermined_value)
                            }
                            contentContainer.background = RippleDrawable(
                                ColorStateList.valueOf(Color.BLACK),
                                null,
                                ShapeDrawable(RectShape())
                            )
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

    class ItemHolder(binding: RuleRecyclerItemBinding) : Holder<RuleRecyclerItemBinding>(binding)

}