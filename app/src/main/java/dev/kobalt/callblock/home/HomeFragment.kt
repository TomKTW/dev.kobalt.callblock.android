package dev.kobalt.callblock.home

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dev.kobalt.callblock.R
import dev.kobalt.callblock.base.BaseFragment
import dev.kobalt.callblock.databinding.HomeBinding
import dev.kobalt.callblock.extension.areAllPermissionsGranted
import dev.kobalt.callblock.extension.launchAppInfo
import kotlinx.coroutines.flow.collect

/** Home fragment. */
class HomeFragment : BaseFragment<HomeBinding>() {

    companion object {
        /** List of all permissions required to detect suspicious incoming calls. */
        val callPermissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
        ).let {
            // Android O uses ANSWER_PHONE_CALLS permission that is needed to end phone calls on P+.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) it.plus(Manifest.permission.ANSWER_PHONE_CALLS) else it
        }
    }

    private val viewModel by viewModels<HomeViewModel>()

    /** Permission request for managing calls. */
    private val callPermissionsRequest = registerForActivityResult(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleScope.launchWhenCreated {
            viewModel.stateFlow.collect {
                viewBinding?.apply {
                    stateTitleLabel.text = getString(
                        if (it) R.string.home_state_enabled_title else R.string.home_state_disabled_title
                    )
                    stateToggleButton.isChecked = it
                }
            }
        }
        viewBinding?.apply {
            permissionContainer.isVisible =
                !requireContext().areAllPermissionsGranted(*callPermissions)
            permissionRequestButton.setOnClickListener {
                callPermissionsRequest.launch(callPermissions)
            }
            stateToggleButton.setOnClickListener { viewModel.toggleState() }
        }
    }

    override fun onResume() {
        super.onResume()
        viewBinding?.apply {
            // Check permission state to see if permissions have been changed after request prompt.
            val permissionsGranted = requireContext().areAllPermissionsGranted(*callPermissions)
            permissionContainer.isVisible = !permissionsGranted
            enableContainer.alpha = if (permissionsGranted) 1.0f else 0.5f
            stateToggleButton.isEnabled = permissionsGranted
        }
    }

}