package dev.kobalt.callblock.home

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import dev.kobalt.callblock.R
import dev.kobalt.callblock.base.BaseFragment
import dev.kobalt.callblock.databinding.HomeBinding
import dev.kobalt.callblock.extension.areAllPermissionsGranted
import dev.kobalt.callblock.extension.launchAppInfo
import dev.kobalt.callblock.rule.RuleFragmentKey
import kotlinx.coroutines.flow.collect

/** Fragment for home page containing main content for this application. */
class HomeFragment : BaseFragment<HomeBinding>() {

    companion object {
        /** Permissions required to check incoming calls. */
        val checkCallPermissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
        ).let {
            // Android O uses ANSWER_PHONE_CALLS permission that is needed to end phone calls on P+.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) it.plus(Manifest.permission.ANSWER_PHONE_CALLS) else it
        }

        /** Permissions required to allow blocking any calls that are not in contacts. */
        val contactPermissions = arrayOf(
            Manifest.permission.READ_CONTACTS
        )
    }

    private val checkCallsPermissionsGranted get() = requireContext().areAllPermissionsGranted(*checkCallPermissions)
    private val contactPermissionsGranted get() = requireContext().areAllPermissionsGranted(*contactPermissions)

    /** View model for home fragment. */
    private val viewModel by viewModels<HomeViewModel>()

    /** Permission request for checking incoming calls. */
    private val checkCallPermissionsRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Display a message if not all permissions were given.
        if (!permissions.all { it.value == true }) AlertDialog.Builder(requireContext()).apply {
            // If user was unable to get permission request prompt, ask for checking app info if possible.
            if (permissions.all { shouldShowRequestPermissionRationale(it.key) }) {
                setTitle(R.string.home_permissions_not_granted_title)
                setMessage(R.string.home_permissions_not_granted_message)
                setNeutralButton(R.string.home_permissions_not_granted_close_action) { _, _ -> }
            } else {
                setTitle(R.string.home_permissions_denied_title)
                setMessage(R.string.home_permissions_denied_message)
                setPositiveButton(R.string.home_permissions_denied_yes_action) { _, _ -> requireContext().launchAppInfo() }
                setNegativeButton(R.string.home_permissions_denied_no_action) { _, _ -> }
            }
        }.show()
    }

    /** Permission request for reading contact list. */
    private val contactPermissionsRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Display a message if not all permissions were given.
        if (!permissions.all { it.value == true }) AlertDialog.Builder(requireContext()).apply {
            // If user was unable to get permission request prompt, ask for checking app info if possible.
            if (permissions.all { shouldShowRequestPermissionRationale(it.key) }) {
                setTitle(R.string.home_permissions_contacts_not_granted_title)
                setMessage(R.string.home_permissions_contacts_not_granted_message)
                setNeutralButton(R.string.home_permissions_not_granted_close_action) { _, _ -> }
            } else {
                setTitle(R.string.home_permissions_contacts_denied_title)
                setMessage(R.string.home_permissions_contacts_denied_message)
                setPositiveButton(R.string.home_permissions_denied_yes_action) { _, _ -> requireContext().launchAppInfo() }
                setNegativeButton(R.string.home_permissions_denied_no_action) { _, _ -> }
            }
        }.show()
    }

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
                        checkCallsPermissionsGranted && viewModel.userRulesFlow.replayCache.firstOrNull() == true
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
        viewLifecycleScope.launchWhenCreated {
            viewModel.countAllowedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.allowedCallCountValueLabel.text = it.toString()
                }
            }
        }
        viewLifecycleScope.launchWhenCreated {
            viewModel.countWarnedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.warnedCallCountValueLabel.text = it.toString()
                }
            }
        }
        viewLifecycleScope.launchWhenCreated {
            viewModel.countBlockedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.blockedCallCountValueLabel.text = it.toString()
                }
            }
        }
        viewLifecycleScope.launchWhenCreated {
            viewModel.countPredefinedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.predefinedRuleCountValueLabel.text = it.toString()
                }
            }
        }
        viewLifecycleScope.launchWhenCreated {
            viewModel.countUserDefinedFlow.collect {
                viewBinding?.apply {
                    overviewContainer.userRuleCountValueLabel.text = it.toString()
                }
            }
        }
        viewBinding?.apply {
            overviewContainer.apply {
                requestCheckCallPermissionsButton.setOnClickListener {
                    checkCallPermissionsRequest.launch(checkCallPermissions)
                }
                requestContactPermissionsButton.setOnClickListener {
                    contactPermissionsRequest.launch(contactPermissions)
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
            }
            footerOverviewButton.setOnClickListener { viewModel.updatePageState(Page.Overview) }
            footerCallsButton.setOnClickListener { viewModel.updatePageState(Page.Calls) }
            footerOptionsButton.setOnClickListener { viewModel.updatePageState(Page.Options) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewBinding?.apply {
            // Check permission states to see if permissions have been changed after request prompt.
            overviewContainer.apply {
                checkCallPermissionsContainer.isVisible = !checkCallsPermissionsGranted
                contactPermissionsContainer.isVisible = !contactPermissionsGranted
            }
            optionsContainer.apply {
                userRuleOption.isEnabled = checkCallsPermissionsGranted
                userEditOption.isEnabled =
                    checkCallsPermissionsGranted && viewModel.userRulesFlow.replayCache.firstOrNull() == true
                predefinedRuleOption.isEnabled = checkCallsPermissionsGranted
                contactRuleOption.isEnabled =
                    checkCallsPermissionsGranted && contactPermissionsGranted
            }
        }
    }

    enum class Page {
        Overview, Calls, Options
    }


}