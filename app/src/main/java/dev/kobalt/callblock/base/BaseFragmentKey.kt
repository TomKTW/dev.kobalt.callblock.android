package dev.kobalt.callblock.base

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey

/**
 * Base navigation key for fragments.
 *
 * Each class has to implement instantiateFragment() to navigate to given fragment location.
 * Values from key object can be used for putting them into arguments in fragments.
 */
abstract class BaseFragmentKey : DefaultFragmentKey()