package com.dzaitsev.rxviper

import com.dzaitsev.rxviper.internal.CallbackAttachment

/** A [Presenter] with an independently attached, weakly held navigation contract. */
abstract class ViperPresenter<V : ViewCallbacks, R : Router>(
    viewType: Class<V>,
    routerType: Class<R>,
    initialView: V? = null,
    initialRouter: R? = null,
) : Presenter<V>(viewType, initialView) {
    private val attachment = CallbackAttachment(routerType, initialRouter)

    protected val router: R get() = attachment.proxy

    fun hasRouter(): Boolean = attachment.current() != null

    fun takeRouter(router: R) = attachment.take(router, ::onTakeRouter, ::onDropRouter)

    fun dropRouter(router: R) = attachment.drop(router, ::onDropRouter)

    protected open fun onTakeRouter(router: R) = Unit

    protected open fun onDropRouter(router: R) = Unit
}
