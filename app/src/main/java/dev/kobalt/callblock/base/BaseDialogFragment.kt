package dev.kobalt.callblock.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/** Base for dialog fragment with view binding initialization. */
abstract class BaseDialogFragment<V : ViewBinding> : DialogFragment() {

    /** Internal ViewBinding object to avoid public mutation. */
    private var binding: V? = null

    /** ViewBinding object containing inflated view for this fragment. */
    val viewBinding: V? get() = binding

    /** ViewBinding generic type class from given parameter. */
    private val viewBindingClass =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>

    /** Inflate method from ViewBinding class. Note that ViewBinding class itself does not have inflate method on it's own, but generated classes that implement it do have it. */
    private val method = viewBindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )

    // Assume that the returned object is always ViewBinding object.
    @Suppress("UNCHECKED_CAST")
    /** Return ViewBinding object that contains inflated view invoked from given class method. */
    protected open fun createBindingInstance(inflater: LayoutInflater, container: ViewGroup?): V {
        return method.invoke(null, inflater, container, false) as V
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        // Build dialog with inflated view and apply binding value.
        return AlertDialog.Builder(
            requireContext(),
            com.google.android.material.R.style.Theme_MaterialComponents_Dialog_Bridge
        ).apply {
            setView(createBindingInstance(layoutInflater, null).also { binding = it }.root)
        }.create().apply {
            setCanceledOnTouchOutside(false)
            onDialogCreated(this, savedInstanceState)
        }
    }

    /** Event method that is called when dialog has been created. */
    open fun onDialogCreated(dialog: Dialog, savedInstanceState: Bundle?) {

    }

    override fun onStart() {
        super.onStart()
        // Make dialog to match parent in width, but wrap for height.
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Destroy binding when view is destroyed.
        binding = null
    }

    /** Lifecycle scope for View.*/
    val viewLifecycleScope get() = viewLifecycleOwner.lifecycleScope

}