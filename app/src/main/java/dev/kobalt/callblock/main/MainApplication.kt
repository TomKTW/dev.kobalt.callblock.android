package dev.kobalt.callblock.main

import dev.kobalt.callblock.base.BaseApplication
import dev.kobalt.callblock.call.CallRepository
import dev.kobalt.callblock.preferences.PreferencesRepository

/** Main application as a base for getting resources. */
class MainApplication : BaseApplication() {

    lateinit var callRepository: CallRepository

    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate() {
        super.onCreate()
        callRepository = CallRepository().also { it.application = this }
        preferencesRepository = PreferencesRepository().also { it.application = this }
    }

}


