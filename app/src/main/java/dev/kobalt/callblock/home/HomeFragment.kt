package dev.kobalt.callblock.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import dev.kobalt.callblock.R
import dev.kobalt.callblock.base.BaseFragment
import dev.kobalt.callblock.databinding.HomeBinding
import dev.kobalt.callblock.extension.isGrantedForCallScreening
import dev.kobalt.callblock.extension.isGrantedToAllowContactCallsOnly
import dev.kobalt.callblock.permission.PermissionFragmentKey
import dev.kobalt.callblock.rule.RuleFragmentKey
import kotlinx.coroutines.flow.collect


/** Fragment for home page containing main content for this application. */
class HomeFragment : BaseFragment<HomeBinding>() {

    /** View model for home fragment. */
    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe page position.
        viewLifecycleScope.launchWhenCreated {
            viewModel.pageState.collect {
                viewBinding?.apply {
                    headerTitleLabel.text = when (it) {
                        Page.Overview -> requireContext().getString(R.string.home_overview_title)
                        Page.Calls -> requireContext().getString(R.string.home_calls_title)
                        Page.Options -> requireContext().getString(R.string.home_options_title)
                    }
                    overviewContainer.root.isVisible = it == Page.Overview
                    callsContainer.root.isVisible = it == Page.Calls
                    optionsContainer.root.isVisible = it == Page.Options
                }
            }
        }
        // Observe predefined rule state.
        viewLifecycleScope.launchWhenCreated {
            viewModel.predefinedRulesFlow.collect {
                viewBinding?.apply {
                    optionsContainer.predefinedRuleOption.optionSwitch.isChecked = it
                }
            }
        }
        // Observe contacts rule state.
        viewLifecycleScope.launchWhenCreated {
            viewModel.contactRulesFlow.collect {
                viewBinding?.apply {
                    optionsContainer.contactRuleOption.optionSwitch.isChecked = it
                }
            }
        }
        // Observe user defined rule state.
        viewLifecycleScope.launchWhenCreated {
            viewModel.userRulesFlow.collect {
                viewBinding?.apply {
                    optionsContainer.userRuleOption.optionSwitch.isChecked = it
                    optionsContainer.userEditOption.isEnabled =
                        requireContext().isGrantedForCallScreening() && viewModel.userRulesFlow.replayCache.firstOrNull() == true
                }
            }
        }
        // Observe call history.
        viewLifecycleScope.launchWhenCreated {
            viewModel.callListFlow.collect {
                viewBinding?.apply {
                    callsContainer.apply {
                        emptyListContainer.isVisible = it.isEmpty()
                        listRecycler.isVisible = it.isNotEmpty()
                        listRecycler.list = it
                    }
                }
            }
        }
        // Observe allowed call count.
        viewLifecycleScope.launchWhenCreated {
            viewModel.countAllowedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.allowedCallCountValueLabel.text = it.toString()
                }
            }
        }
        // Observe warned call count.
        viewLifecycleScope.launchWhenCreated {
            viewModel.countWarnedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.warnedCallCountValueLabel.text = it.toString()
                }
            }
        }
        // Observe blocked call count.
        viewLifecycleScope.launchWhenCreated {
            viewModel.countBlockedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.blockedCallCountValueLabel.text = it.toString()
                }
            }
        }
        // Observe predefined rule count.
        viewLifecycleScope.launchWhenCreated {
            viewModel.countPredefinedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.predefinedRuleCountValueLabel.text = it.toString()
                }
            }
        }
        // Observe user rule count.
        viewLifecycleScope.launchWhenCreated {
            viewModel.countUserDefinedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.userRuleCountValueLabel.text = it.toString()
                }
            }
        }
        viewBinding?.apply {
            overviewContainer.apply {
                reviewPermissionsButton.setOnClickListener {
                    backstack.goTo(PermissionFragmentKey())
                }
            }
            optionsContainer.apply {
                predefinedRuleOption.setOnClickListener {
                    viewModel.updatePredefinedRules(!predefinedRuleOption.optionSwitch.isChecked)
                }
                contactRuleOption.setOnClickListener {
                    viewModel.updateContactRules(!contactRuleOption.optionSwitch.isChecked)
                }
                userRuleOption.setOnClickListener {
                    viewModel.updateUserRules(!userRuleOption.optionSwitch.isChecked)
                }
                userEditOption.setOnClickListener {
                    backstack.goTo(RuleFragmentKey())
                }
                permissionsOption.setOnClickListener {
                    backstack.goTo(PermissionFragmentKey())
                }
            }
            footerOverviewButton.setOnClickListener { viewModel.updatePageState(Page.Overview) }
            footerCallsButton.setOnClickListener { viewModel.updatePageState(Page.Calls) }
            footerOptionsButton.setOnClickListener { viewModel.updatePageState(Page.Options) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewBinding?.apply {
            // Check permission states to see if permissions have been changed.
            overviewContainer.apply {
                reviewPermissionsContainer.isVisible = !requireContext().isGrantedForCallScreening()
            }
            optionsContainer.apply {
                userRuleOption.isEnabled = requireContext().isGrantedForCallScreening()
                userEditOption.isEnabled =
                    requireContext().isGrantedForCallScreening() && viewModel.userRulesFlow.replayCache.firstOrNull() == true
                predefinedRuleOption.isEnabled = requireContext().isGrantedForCallScreening()
                contactRuleOption.isEnabled = requireContext().isGrantedToAllowContactCallsOnly()
            }
        }
    }

    enum class Page {
        Overview, Calls, Options
    }

}