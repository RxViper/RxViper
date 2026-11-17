# Migrating to RxViper 2

Version 2 is a Kotlin rewrite with a new artifact layout. It is not a drop-in
replacement for the old `com.dzaitsev.rxviper:rxviper` jar.

| Before | After |
| --- | --- |
| `rxviper` with one embedded RxJava major | `rxviper-core` plus the chosen backend artifact |
| Root-package `Interactor` | `com.dzaitsev.rxviper.rxjava.Interactor`, `.rxjava2.Interactor`, `.rxjava3.Interactor` |
| Inferred presenter generic classes | Explicit `View::class.java` and `Router::class.java` constructor parameters |
| `getView()` / `getRouter()` | Protected Kotlin `view` / `router` properties |
| Nullable omitted request | Explicit typed request; `Unit` for no input, nullable request type when meaningful |
| `execute(callbacks..., request)` | `execute(request, onNext, onError, onComplete)`; returns native handle |
| `dispose()` clears subscriptions for reuse | `clear()` allows reuse; `dispose()` / Rx1 `unsubscribe()` permanently closes execution |
| `isDisposed` means no active work | `isDisposed` means permanently closed; a new/cleared interactor is active |
| `Mapper` implements Rx function | Pure Kotlin functional interface; pass `mapper::map` to operators, use `mapAll` for collections |
| `createFlowable(request)` called before scheduling | Cold deferred source creation happens on subscription scheduler |
| Old Gradle plugin Java templates | Kotlin templates with explicit backend selector and lazy Property DSL |
| JCenter/Bintray/Retrolambda/APT | Maven Central/Google, Kotlin JVM, built-in Android Kotlin, `maven-publish` |

RxJava 1 interactors implement `createObservable`. RxJava 2 and 3 implement
`createFlowable` and expose backpressure through native sources. The former
subscriber overload is replaced by `interactor(request).subscribe(subscriber)`;
that subscription is caller-owned, so cancel it separately. Use the callback
`execute` overload for interactor-managed executions.

Coroutine users implement `SuspendInteractor.run` for one result or
`FlowInteractor.createFlow` for streams. Inject a dispatcher and use `invoke`
in the caller's coroutine/collector, or the scope-taking `execute` helper. The
caller owns its scope and cancels each returned Job on view detach. Cancellation
is never converted into an ordinary error. No Rx type crosses into this module.

Lifecycle hook parameters remain supported: `onTakeView(view)`, `onDropView(view)`,
`onTakeRouter(router)`, `onDropRouter(router)`. Use the protected proxy properties
inside asynchronous callbacks to avoid capturing an actual Activity or router.
Explicit class tokens replace the old generic reflection inference; no shrinker
rules are needed to retain generic superclass signatures for presenter discovery.

Callbacks remain weakly held, Unit-only interfaces. Detached proxy calls are no-ops.
Keep lifecycle operations and callback delivery on the same UI thread. Retaining
an Activity in a local hook parameter and then capturing it in long-lived work
still creates a strong reference; prefer `this.view` in those callbacks.

The Gradle generator is a starter-code tool. Configure backend, screens and use
cases; run `generateRxViper`; copy the generated Kotlin files into your source tree,
then implement them. Its output directory is replaced on generation. See the
[plugin DSL migration](rxviper-gradle-plugin/README.md) for renamed/removed options.

Builds require JDK17+ and use pinned Gradle9.9.1/Kotlin2.4.21. Runtime jars target
JVM8 bytecode; the Gradle plugin targets JVM17. Kotlin consumers must understand
Kotlin2.4 metadata. Android runtime modules remain ordinary JVM jars; the SDK is
required only when opting into the sample with `-PandroidSample=true`.

The snapshot can be published to `build/repository` with
`./gradlew publishAllPublicationsToBuildRepository`. Public repository release
credentials, signing and remote publication are outside this local rewrite.
