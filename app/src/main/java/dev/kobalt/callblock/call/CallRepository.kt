package dev.kobalt.callblock.call

import dev.kobalt.callblock.main.MainApplication

/** Repository for managing call state. */
class CallRepository {

    /** Reference to main application. */
    var application: MainApplication? = null

    /** Returns the state of given number to indicate if it's suspicious, scam or normal. */
    fun getState(number: String) = when (number) {
        "4259501212" -> CallState.Suspicious
        "2539501212" -> CallState.Scam
        else -> CallState.Normal
    }

}

