package dev.kobalt.callblock.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import java.lang.reflect.ParameterizedType

/** Base for Fragment with view binding initialization. */
abstract class BaseFragment<V : ViewBinding> : KeyedFragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate view and apply binding value.
        return createBindingInstance(inflater, container).also { binding = it }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Destroy binding when view is destroyed.
        binding = null
    }

    /** Invoke event when back button is pressed. By default, it will navigate back to previous navigation entry. */
    open fun onBackPressed(): Boolean {
        return backstack.goBack()
    }

    /** Lifecycle scope for View.*/
    val viewLifecycleScope get() = viewLifecycleOwner.lifecycleScope

}