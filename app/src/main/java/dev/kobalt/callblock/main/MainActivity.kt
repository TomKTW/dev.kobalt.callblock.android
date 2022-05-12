package dev.kobalt.callblock.main

import android.os.Bundle
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import dev.kobalt.callblock.R
import dev.kobalt.callblock.base.BaseActivity
import dev.kobalt.callblock.base.BaseFragment
import dev.kobalt.callblock.base.BaseFragmentKey
import dev.kobalt.callblock.databinding.MainBinding
import dev.kobalt.callblock.home.HomeFragmentKey

/** Main activity of application. */
class MainActivity : BaseActivity<MainBinding>(), SimpleStateChanger.NavigationHandler {

    /** State change object for navigation. */
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Prepare and install navigation to container view.
        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)
        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, viewBinding.container, History.single(HomeFragmentKey()))
    }

    override fun onBackPressed() {
        // Find top fragment matching the tags in history stack.
        (supportFragmentManager.fragments.find {
            it.tag == (backstack.getHistory<BaseFragmentKey>().last())?.fragmentTag
        } as? BaseFragment<*>)?.let { fragment ->
            // Invoke top fragment's onBackPressed() method if there is any.
            if (!fragment.onBackPressed()) super.onBackPressed()
        } ?: run {
            // Otherwise, exit activity by invoking onBackPressed().
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }

}