# RxViper core

This Kotlin/JVM module depends only on the Kotlin standard library. It contains
`Presenter`, `ViperPresenter`, `ViewCallbacks`, `Router`, and `Mapper` in
`com.dzaitsev.rxviper`.

Presenters take explicit interface class tokens. This avoids runtime discovery of
generic type arguments and works with intermediate generic presenter subclasses.

```kotlin
interface ProfileView : ViewCallbacks {
    fun showName(name: String)
}

class ProfilePresenter : Presenter<ProfileView>(ProfileView::class.java) {
    fun display(name: String) = view.showName(name)
}
```

The protected `view` and `router` properties expose stable proxies. Detached calls
are ignored. Contracts must be interfaces whose methods return `Unit`/`void`;
getters and suspend methods are rejected during construction. `equals`, `hashCode`,
and `toString` always describe the proxy itself. Exceptions from an attached
callback propagate without an `InvocationTargetException` wrapper.

Targets are held weakly. The application must retain a view or router while it
should stay attached. All lifecycle operations and callback delivery belong on one
UI thread; the core imposes no Android or scheduling dependency.

Repeated takes of the same target do nothing. Replacements drop the old target
before taking the new one, and stale drops are ignored using reference identity.
`onTakeView` runs after attachment and `onDropView` runs before detachment; routers
follow the same rules. Constructors with initial targets do not invoke hooks.
Drop cleanup runs even if a hook throws. A replacement attached reentrantly during
a drop hook survives cleanup and takes priority over the outer replacement.

`Mapper` is a backend-neutral functional interface. Use `mapper::map` with Rx
operators, `mapper(value)` for a direct call, and `mapper.mapAll(values)` for an
ordered list. No async interactor contract is forced into core: suspend functions,
Flow, Observable, and Flowable retain their native APIs in the adapter modules.
