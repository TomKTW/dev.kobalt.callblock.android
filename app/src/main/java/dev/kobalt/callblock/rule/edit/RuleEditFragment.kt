package dev.kobalt.callblock.rule.edit

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.kobalt.callblock.R
import dev.kobalt.callblock.base.BaseDialogFragment
import dev.kobalt.callblock.databinding.RuleEditBinding
import dev.kobalt.callblock.extension.getResourceColor
import dev.kobalt.callblock.extension.inputMethodManager
import dev.kobalt.callblock.extension.showToast
import dev.kobalt.callblock.rule.RuleEntity
import kotlinx.coroutines.flow.collect

/** Fragment for editing given user made rule. */
class RuleEditFragment : BaseDialogFragment<RuleEditBinding>() {

    private val viewModel by viewModels<RuleEditViewModel>()

    /** ID of the provided rule. If null, treat it as adding a new rule. */
    private val ruleId get() = arguments?.get("id") as? Long

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.load(ruleId)
    }

    override fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.numberFlow.collect {
                viewBinding?.apply { numberInput.editText.setText(it) }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.actionFlow.collect {
                viewBinding?.apply {
                    /** Returns a drawable with background and ripple effect with given colors. */
                    fun getRippleDrawable(rippleColor: Int, backgroundColor: Int) = LayerDrawable(
                        arrayOf(
                            backgroundColor.toDrawable(),
                            RippleDrawable(
                                ColorStateList.valueOf(rippleColor),
                                null,
                                ShapeDrawable(RectShape())
                            )
                        )
                    )
                    // Apply background and text color depending on selected action.
                    ruleAllowButton.apply {
                        background = getRippleDrawable(
                            requireContext().getResourceColor(R.color.primary_dark),
                            requireContext().getResourceColor(if (it == RuleEntity.Action.Allow) R.color.primary_normal else R.color.white)
                        )
                        setTextColor(requireContext().getResourceColor(if (it == RuleEntity.Action.Allow) R.color.white else R.color.primary_normal))
                    }
                    ruleWarnButton.apply {
                        background = getRippleDrawable(
                            requireContext().getResourceColor(R.color.primary_dark),
                            requireContext().getResourceColor(if (it == RuleEntity.Action.Warn) R.color.primary_normal else R.color.white)
                        )
                        setTextColor(requireContext().getResourceColor(if (it == RuleEntity.Action.Warn) R.color.white else R.color.primary_normal))
                    }
                    ruleBlockButton.apply {
                        background = getRippleDrawable(
                            requireContext().getResourceColor(R.color.primary_dark),
                            requireContext().getResourceColor(if (it == RuleEntity.Action.Block) R.color.primary_normal else R.color.white)
                        )
                        setTextColor(requireContext().getResourceColor(if (it == RuleEntity.Action.Block) R.color.white else R.color.primary_normal))
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.saveFlow.collect {
                // Disable all inputs while rule is being saved.
                viewBinding?.apply {
                    ruleAllowButton.isEnabled = it != RuleEditViewModel.SaveState.Saving
                    ruleWarnButton.isEnabled = it != RuleEditViewModel.SaveState.Saving
                    ruleBlockButton.isEnabled = it != RuleEditViewModel.SaveState.Saving
                    backButton.isEnabled = it != RuleEditViewModel.SaveState.Saving
                    deleteButton.isEnabled = it != RuleEditViewModel.SaveState.Saving
                    submitButton.isEnabled = it != RuleEditViewModel.SaveState.Saving
                    numberInput.isEnabled = it != RuleEditViewModel.SaveState.Saving
                }
                when (it) {
                    RuleEditViewModel.SaveState.Success -> dismiss()
                    RuleEditViewModel.SaveState.AlreadyExists -> requireContext().showToast(
                        getString(R.string.rule_edit_save_already_exists_message)
                    )
                    RuleEditViewModel.SaveState.Failure -> requireContext().showToast(
                        getString(R.string.rule_edit_save_failure_message)
                    )
                    else -> {}
                }
            }
        }
        viewBinding?.apply {
            // Focus on number input first.
            root.post {
                requireContext().inputMethodManager.showSoftInput(
                    numberInput.editText,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }
            backButton.setOnClickListener { dismiss() }
            // Display form as adding an rule if there is no given ID.
            headerTitleLabel.text =
                (if (ruleId == null) getString(R.string.rule_edit_add_title) else getString(
                    R.string.rule_edit_edit_title
                ))
            deleteButton.apply {
                isVisible = ruleId != null
                setOnClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(context.getString(R.string.rule_edit_delete_prompt_title))
                        setMessage(context.getString(R.string.rule_edit_delete_prompt_message))
                        setPositiveButton(context.getString(R.string.rule_edit_delete_prompt_confirm_action)) { _, _ -> viewModel.delete(); dismiss() }
                        setNegativeButton(context.getString(R.string.rule_edit_delete_prompt_cancel_action)) { _, _ -> }
                    }.show()
                }
            }
            // Before submitting, check if number and action are defined.
            submitButton.setOnClickListener {
                when {
                    numberInput.phoneNumber.isEmpty() -> {
                        requireContext().showToast(getString(R.string.rule_edit_submit_phone_number_required_message))
                    }
                    viewModel.actionFlow.replayCache.firstOrNull() == null -> {
                        requireContext().showToast(getString(R.string.rule_edit_submit_action_required_message))
                    }
                    !numberInput.isValid -> {
                        // Warn user if number might not be valid, assume that scammers can use invalid phone numbers.
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle(getString(R.string.rule_edit_submit_phone_number_invalid_prompt_title))
                            setMessage(getString(R.string.rule_edit_submit_phone_number_invalid_prompt_message))
                            setPositiveButton(getString(R.string.rule_edit_submit_phone_number_invalid_prompt_confirm_action)) { _, _ ->
                                viewModel.updateNumber(numberInput.phoneNumber)
                                viewModel.save()
                            }
                            setNegativeButton(getString(R.string.rule_edit_submit_phone_number_invalid_prompt_cancel_action)) { _, _ -> }
                        }.show()
                    }
                    else -> {
                        viewModel.updateNumber(numberInput.phoneNumber)
                        viewModel.save()
                    }
                }
            }
            ruleAllowButton.setOnClickListener { viewModel.updateAction(RuleEntity.Action.Allow) }
            ruleWarnButton.setOnClickListener { viewModel.updateAction(RuleEntity.Action.Warn) }
            ruleBlockButton.setOnClickListener { viewModel.updateAction(RuleEntity.Action.Block) }
            numberInput.requestFocus()
        }
    }

}