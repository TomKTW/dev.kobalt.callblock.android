package dev.kobalt.callblock.home

import dev.kobalt.callblock.base.BaseFragmentKey
import kotlinx.parcelize.Parcelize

/** Navigation key for Home fragment. */
@Parcelize // Placeholder must be used as data class requires at least one parameter.
data class HomeFragmentKey(private val placeholder: String = "") : BaseFragmentKey() {
    override fun instantiateFragment() = HomeFragment()
}