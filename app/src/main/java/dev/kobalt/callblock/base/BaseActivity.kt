package dev.kobalt.callblock.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/** Base for activity with view binding initialization. */
abstract class BaseActivity<V : ViewBinding> : AppCompatActivity() {

    /** Internal ViewBinding object to avoid public mutation. */
    private lateinit var binding: V

    /** ViewBinding object containing inflated view for this activity. */
    val viewBinding: V get() = binding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate view and apply binding value.
        binding = createBindingInstance(LayoutInflater.from(this), null)
        // Apply view as root view.
        setContentView(binding.root)
    }

}