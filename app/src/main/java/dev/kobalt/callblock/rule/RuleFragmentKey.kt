package dev.kobalt.callblock.rule

import dev.kobalt.callblock.base.BaseFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize // Placeholder must be used as data class requires at least one parameter.
data class RuleFragmentKey(private val placeholder: String = "") : BaseFragmentKey() {
    override fun instantiateFragment() = RuleFragment()
}