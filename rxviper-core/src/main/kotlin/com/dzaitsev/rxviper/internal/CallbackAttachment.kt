package com.dzaitsev.rxviper.internal

import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/** Lifecycle state and a stable no-op proxy. Access only from the presenter's UI thread. */
internal class CallbackAttachment<T : Any>(
    type: Class<T>,
    initial: T?,
) {
    private var reference: WeakReference<T>? = initial?.let(::WeakReference)
    private var dropping: T? = null

    val proxy: T

    init {
        require(type.isInterface) { "${type.name} must be an interface" }
        require(type.methods.all { isObjectMethod(it) || it.returnType == Void.TYPE }) {
            "${type.name} may only declare Unit/void callbacks; getters and suspend callbacks are unsupported"
        }
        require(initial == null || type.isInstance(initial)) { "Initial callback must implement ${type.name}" }
        val handler = InvocationHandler { proxy, method, arguments ->
            when {
                method.name == "equals" && method.parameterTypes.contentEquals(arrayOf(Any::class.java)) ->
                    proxy === arguments?.get(0)
                method.name == "hashCode" && method.parameterTypes.isEmpty() -> System.identityHashCode(proxy)
                method.name == "toString" && method.parameterTypes.isEmpty() ->
                    "RxViper proxy<${type.name}> (${if (current() == null) "detached" else "attached"})"
                else -> current()?.let { target -> invoke(method, target, arguments) }
            }
        }
        proxy = type.cast(Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler))
    }

    fun current(): T? = reference?.get()

    fun take(target: T, onTake: (T) -> Unit, onDrop: (T) -> Unit) {
        val previous = current()
        if (previous === target) return
        if (previous != null) drop(previous, onDrop)
        // A callback may attach a replacement while the old target is being dropped.
        if (current() != null && dropping == null) return
        reference = WeakReference(target)
        onTake(target)
    }

    fun drop(target: T, onDrop: (T) -> Unit) {
        if (current() !== target || dropping === target) return
        val droppedReference = reference
        val previousDropping = dropping
        dropping = target
        try {
            onDrop(target)
        } finally {
            dropping = previousDropping
            // Never clear a different target attached reentrantly by a hook.
            if (reference === droppedReference) {
                reference?.clear()
                reference = null
            }
        }
    }

    private fun invoke(method: Method, target: T, arguments: Array<out Any?>?): Any? {
        try {
            return try {
                method.invoke(target, *(arguments ?: emptyArray()))
            } catch (_: IllegalAccessException) {
                // Package-private callback interfaces are common in application code.
                @Suppress("DEPRECATION")
                method.isAccessible = true
                method.invoke(target, *(arguments ?: emptyArray()))
            }
        } catch (exception: InvocationTargetException) {
            throw exception.targetException
        }
    }

    private fun isObjectMethod(method: Method): Boolean = when (method.name) {
        "equals" -> method.parameterTypes.contentEquals(arrayOf(Any::class.java)) &&
            method.returnType == Boolean::class.javaPrimitiveType
        "hashCode" -> method.parameterTypes.isEmpty() && method.returnType == Int::class.javaPrimitiveType
        "toString" -> method.parameterTypes.isEmpty() && method.returnType == String::class.java
        else -> false
    }
}
