package dev.kobalt.callblock.permission

import dev.kobalt.callblock.base.BaseFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize // Placeholder must be used as data class requires at least one parameter.
data class PermissionFragmentKey(private val placeholder: String = "") : BaseFragmentKey() {
    override fun instantiateFragment() = PermissionFragment()
}