package dev.kobalt.callblock.main

import dev.kobalt.callblock.base.BaseApplication
import dev.kobalt.callblock.call.CallRepository

/** Main application as a base for getting resources. */
class MainApplication : BaseApplication() {

    lateinit var callRepository: CallRepository

    override fun onCreate() {
        super.onCreate()
        callRepository = CallRepository().also { it.application = this }
    }

}


