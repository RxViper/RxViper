package com.dzaitsev.rxviper

import com.dzaitsev.rxviper.internal.CallbackAttachment

/**
 * Owns presentation behavior independently of any async library or platform.
 *
 * [view] is a stable proxy: calls are ignored while detached. Only Unit-returning
 * interface callbacks are supported. The actual view is held weakly, so application
 * code must retain it for as long as it should remain attached.
 *
 * Call lifecycle methods and access callbacks from a single UI thread. Initial
 * attachment does not call overridable hooks during construction.
 */
abstract class Presenter<V : ViewCallbacks>(
    viewType: Class<V>,
    initialView: V? = null,
) {
    private val attachment = CallbackAttachment(viewType, initialView)

    protected val view: V get() = attachment.proxy

    fun hasView(): Boolean = attachment.current() != null

    /** Attaches once per identity, dropping any previous view first. */
    fun takeView(view: V) = attachment.take(view, ::onTakeView, ::onDropView)

    /** Ignores stale drops. The hook runs while the view is still attached. */
    fun dropView(view: V) = attachment.drop(view, ::onDropView)

    protected open fun onTakeView(view: V) = Unit

    protected open fun onDropView(view: V) = Unit
}
