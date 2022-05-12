package dev.kobalt.callblock.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.kobalt.callblock.main.MainApplication

/** Base for ViewModel. AndroidViewModel is used to get reference to Application object where other objects are available. */
open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    /** Main application instance. Wrapped for simplicity purposes. */
    inline val app get() = getApplication<MainApplication>()

}