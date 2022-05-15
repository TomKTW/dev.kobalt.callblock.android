package dev.kobalt.callblock.rule

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import dev.kobalt.callblock.base.BaseFragment
import dev.kobalt.callblock.databinding.RuleBinding
import dev.kobalt.callblock.rule.edit.RuleEditFragment
import kotlinx.coroutines.flow.collect

/** Fragment for managing user defined rules. */
class RuleFragment : BaseFragment<RuleBinding>() {

    private val viewModel by viewModels<RuleViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleScope.launchWhenCreated {
            viewModel.listFlow.collect {
                viewBinding?.apply { listRecycler.list = it }
            }
        }
        viewBinding?.apply {
            backButton.setOnClickListener { backstack.goBack() }
            addButton.setOnClickListener { openRuleEdit() }
            listRecycler.onItemSelect = { openRuleEdit(it.id) }
        }
    }

    /** Displays editor for user made rule.*/
    private fun openRuleEdit(id: Long? = null) = RuleEditFragment().apply {
        arguments = bundleOf("id" to id)
    }.show(childFragmentManager, "RuleEdit")

}