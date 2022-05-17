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
        /** Permissions required to detect suspicious incoming calls. */
        val detectPermissions = arrayOf(
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

    /** View model for home fragment. */
    private val viewModel by viewModels<HomeViewModel>()

    /** Permission request for managing calls to detect suspicious ones. */
    private val detectPermissionsRequest = registerForActivityResult(
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
        // Monitor detection state.
        viewLifecycleScope.launchWhenCreated {
            viewModel.predefinedRulesFlow.collect {
                viewBinding?.apply { detectToggleButton.isChecked = it }
            }
        }
        viewLifecycleScope.launchWhenCreated {
            viewModel.contactRulesFlow.collect {
                viewBinding?.apply { contactsToggleButton.isChecked = it }
            }
        }
        viewLifecycleScope.launchWhenCreated {
            viewModel.userRulesFlow.collect {
                viewBinding?.apply { userRuleToggleButton.isChecked = it }
            }
        }
        viewLifecycleScope.launchWhenCreated {
            viewModel.callListFlow.collect {
                viewBinding?.apply { callContainer.listRecycler.list = it }
            }
        }
        viewBinding?.apply {
            contactsPermissionContainer.isVisible =
                !requireContext().areAllPermissionsGranted(*detectPermissions)
            contactsPermissionRequestButton.setOnClickListener {
                contactPermissionsRequest.launch(contactPermissions)
            }
            contactsToggleButton.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateContactRules(isChecked)
            }
            detectPermissionContainer.isVisible =
                !requireContext().areAllPermissionsGranted(*contactPermissions)
            detectPermissionRequestButton.setOnClickListener {
                detectPermissionsRequest.launch(detectPermissions)
            }
            detectToggleButton.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updatePredefinedRules(isChecked)
            }
            userRuleToggleButton.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateUserRules(isChecked)
            }
            ruleButton.setOnClickListener {
                backstack.goTo(RuleFragmentKey())
            }
            overviewButton.setOnClickListener {
                overviewContainer.isVisible = true
                callContainer.root.isVisible = false
            }
            callsButton.setOnClickListener {
                overviewContainer.isVisible = false
                callContainer.root.isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewBinding?.apply {
            // Check permission states to see if permissions have been changed after request prompt.
            val detectPermissionsGranted =
                requireContext().areAllPermissionsGranted(*detectPermissions)
            detectPermissionContainer.isVisible = !detectPermissionsGranted
            detectToggleButton.isEnabled = detectPermissionsGranted
            val contactPermissionsGranted =
                requireContext().areAllPermissionsGranted(*contactPermissions)
            contactsPermissionContainer.isVisible = !contactPermissionsGranted
            contactsToggleButton.isEnabled = contactPermissionsGranted
        }
    }

}