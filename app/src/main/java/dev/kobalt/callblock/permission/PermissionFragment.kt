package dev.kobalt.callblock.permission

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import dev.kobalt.callblock.R
import dev.kobalt.callblock.base.BaseFragment
import dev.kobalt.callblock.databinding.PermissionBinding
import dev.kobalt.callblock.extension.*

/** Fragment for managing permissions required for app functionality. */
class PermissionFragment : BaseFragment<PermissionBinding>() {

    private val grantAllRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            showDefaultDialerDialog()
        } else {
            showPermissionDialogIfDenied(permissions)
        }
    }

    private val grantSingleRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { showPermissionDialogIfDenied(it) }

    private fun showPermissionDialogIfDenied(permissions: Map<String, Boolean>) {
        if (permissions.any { !it.value && !shouldShowRequestPermissionRationale(it.key) }) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Information")
                setMessage(
                    when (permissions.count { !it.value }) {
                        1 -> "Requested permission was not granted. To grant this permission, you may have to open application info where you can grant it. Would you like to open it now?"
                        else -> "Not all requested permissions were granted. To grant these permissions, you may have to open application info where you can grant it. Would you like to open it now?"
                    }
                )
                setPositiveButton("Yes") { _, _ -> requireContext().launchAppInfo() }
                setNegativeButton("No") { _, _ -> }
            }.show()
        }
    }

    private fun showDefaultDialerDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(
                    TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                    requireContext().packageName
                )
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.apply {
            backButton.setOnClickListener {
                backstack.goBack()
            }
            grantAllButton.apply {
                isVisible =
                    !(requireContext().isDefaultDialer() && requireContext().areAllPermissionsGranted(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE
                    ))
                setOnClickListener {
                    grantAllRequest.launch(
                        arrayOf(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CALL_PHONE
                        ).let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                it.plus(Manifest.permission.ANSWER_PHONE_CALLS)
                            } else {
                                it
                            }
                        })
                }
            }
            defaultDialerOption.apply {
                isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                setOnClickListener {
                    showDefaultDialerDialog()
                }
            }
            readPhoneStateOption.setOnClickListener {
                grantSingleRequest.launch(arrayOf(Manifest.permission.READ_PHONE_STATE))
            }
            readCallLogsOption.setOnClickListener {
                grantSingleRequest.launch(arrayOf(Manifest.permission.READ_CALL_LOG))
            }
            readContactsOption.setOnClickListener {
                grantSingleRequest.launch(arrayOf(Manifest.permission.READ_CONTACTS))
            }
            callPhoneOption.setOnClickListener {
                grantSingleRequest.launch(arrayOf(Manifest.permission.CALL_PHONE))
            }
            answerPhoneCallsOption.apply {
                isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        grantSingleRequest.launch(arrayOf(Manifest.permission.ANSWER_PHONE_CALLS))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewBinding?.apply {
            listOf(
                defaultDialerOption to requireContext().isDefaultDialer(),
                readPhoneStateOption to requireContext().isPermissionGranted(Manifest.permission.READ_PHONE_STATE),
                readCallLogsOption to requireContext().isPermissionGranted(Manifest.permission.READ_CALL_LOG),
                readContactsOption to requireContext().isPermissionGranted(Manifest.permission.READ_CONTACTS),
                callPhoneOption to requireContext().isPermissionGranted(Manifest.permission.CALL_PHONE),
            ).let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) it.plus(
                    answerPhoneCallsOption to requireContext().isPermissionGranted(
                        Manifest.permission.ANSWER_PHONE_CALLS
                    )
                ) else it
            }.forEach {
                it.first.optionButton.text = requireContext().getString(
                    if (it.second) R.string.permission_item_granted_action else R.string.permission_item_grant_action
                )
                it.first.optionButton.setTextColor(
                    requireContext().getResourceColor(
                        if (it.second) R.color.black else R.color.primary_normal
                    )
                )
                it.first.isClickable = !it.second
                it.first.isFocusable = !it.second
            }
        }
    }

}