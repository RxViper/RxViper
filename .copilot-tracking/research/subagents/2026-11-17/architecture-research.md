<!-- markdownlint-disable-file -->
# Architecture research: Kotlin modular rewrite

Date: 2026-11-17. Scope: repository evidence only; no external dependency-version claims.

## Findings

- `rxviper/src/main/java/com/dzaitsev/rxviper/Interactor.java` already targets RxJava 2 (`io.reactivex.Flowable`, `CompositeDisposable`). The current checkout is ahead of the README's 1.0.0-rc3 description.
- `Presenter`, `ViperPresenter`, `ViewCallbacks`, and `Router` have no reactive-library dependency apart from Javadoc references. They are the natural core.
- `Mapper` unnecessarily implements RxJava 2 `Function`; its useful behavior is a single-item mapping plus ordered collection mapping. A Kotlin functional interface or abstract mapper with `map` and `invoke` can live in core and callers can use `mapper::map` in every backend.
- Presenters currently infer generic view/router interface arguments using runtime reflection, create Java proxies, and weakly retain attached targets. Proxy methods must return void. Detached view/router calls are silently dropped.
- Lifecycle tests require identity comparison, ignoring stale drops, idempotent repeated takes, dropping the old target before attaching a new one, `onTake` after attachment, and `onDrop` before detachment. Constructors attach initial targets without invoking overridable callbacks.
- `Interactor.dispose()` calls reusable `CompositeDisposable.clear()`. `isDisposed()` reports whether its bag is empty rather than a terminal state. Rewriting this literally would preserve an API contract violation.
- `RxViperSubscriber` wraps a Reactive Streams subscriber but never calls downstream `onSubscribe`, hiding proper request/cancel and backpressure semantics. Avoid carrying that wrapper forward.
- The Gradle plugin is already Kotlin but emits Java with JavaPoet and is directly coupled to the old Rx2 interactor and root-package types. The Android sample is Java and its presenter depends on reusable `dispose()` behavior.
- No repository AGENTS.md or .github instruction files were found by scoped file enumeration.

## Selected architecture recommendation

Use five independently published JVM libraries:

```text
rxviper-core                    Kotlin stdlib only
rxviper-rxjava  -> core + Rx1   rx.Observable
rxviper-rxjava2 -> core + Rx2   io.reactivex.Flowable
rxviper-rxjava3 -> core + Rx3   io.reactivex.rxjava3.core.Flowable
rxviper-coroutines -> core + kotlinx-coroutines-core
```

Keep existing core package `com.dzaitsev.rxviper`; use distinct backend packages such as `.rxjava`, `.rxjava2`, `.rxjava3`, and `.coroutines`. Matching short names are then convenient and can coexist using import aliases. The adapters must have no dependencies on one another or on Android/RxAndroid.

Core owns presenter lifecycle, markers, mapper, and (only if required by implementation) a tiny backend-neutral cancellation abstraction. Do not force one shared interactor base class: Kotlin cannot abstract over suspend, Flow, Observable, and Flowable without either erasing useful typing or inventing a much larger abstraction. Duplicating the small, backend-native interactor surface across three Rx generations is preferable to hiding their subscription types.

Prefer direct Kotlin nullable view/router references with safe calls over reflective null-object proxies in a breaking rewrite. This removes runtime generic-type discovery, interface-only restrictions, void-only restrictions, and wrapped reflection exceptions. Retain weak targets if preserving historical leak resistance is a priority, but document that weak attachment can disappear after garbage collection; strong references plus explicit lifecycle have more predictable semantics. Parent orchestrator should make this tradeoff once and document it in migration notes. Whichever storage is selected, preserve identity/stale-drop/order behavior from old tests and document single-threaded presenter lifecycle.

## API and lifecycle recommendations

- Core: `Presenter<V : ViewCallbacks>`, `ViperPresenter<V : ViewCallbacks, R : Router>`, `takeView`, `dropView`, `hasView`, protected view accessor, equivalent router lifecycle, no initial-constructor callback invocation.
- Mapper: backend-neutral `map(input)`; callable or method reference; optional ordered `mapAll(Iterable)` avoids awkward overload resolution for generic collection inputs.
- Rx: require explicit request values; use `Unit` for no-argument use cases. Expose a cold Observable/Flowable with injected subscription and observation schedulers; defer source creation so thrown factory exceptions become stream errors and creation occurs at subscription on the injected scheduler.
- If keeping managed `execute`, return the actual Subscription/Disposable for individual cancellation. `clear()` cancels active work and permits reuse; `unsubscribe()`/`dispose()` is terminal and its status must follow backend convention. Avoid Rx internal APIs and do not implement a custom Reactive Streams bridge.
- Coroutines: one suspend interactor for one response, one Flow interactor for many responses. Inject execution dispatcher/context. Keep caller scope ownership; never use GlobalScope or silently own a scope inside a business use case. Use `withContext(dispatcher)` for suspend work and `flowOn(dispatcher)` for upstream Flow work; collection/downstream UI logic stays in caller context. CancellationException must propagate and cancellation must not become ordinary error results.
- Presenter callbacks should not automatically cancel arbitrary shared interactors: explicit lifecycle ownership remains in application code. A sample can cancel its own work in `onDropView`, then create new work on reattachment.

## Alternatives evaluated

1. Keep proxies and translate all Java literally: minimizes API change but retains reflection/weak-reference quirks and makes idiomatic Kotlin harder. Not preferred for complete rewrite.
2. Universal `Interactor<Request, AsyncResponse>` in core: works only as a weak synchronous factory abstraction; cannot model suspend invocation with the same meaningful contract. Do not add merely for nominal reuse.
3. Core plus native adapters: selected; smallest independently consumable artifacts, no cross-backend transitive dependency leakage, familiar types and cancellation.

## Verification required

- Core tests: duplicate take, replacement callback order, stale drop, callback visibility, constructor behavior, mapper behavior.
- Every Rx generation: request forwarding, deferred factory failure, source and observer scheduler use, individual cancellation, clear/reuse, terminal disposal, completion/error cleanup.
- Coroutines: caller cancellation reaches work, dispatcher isolation, suspend exceptions propagate, Flow emissions and failure, upstream/downstream context separation.
- Build/publishing: each module consumable independently; core runtime dependency graph contains no Rx, Reactive Streams, kotlinx-coroutines, or Android artifacts.
- Plugin and sample: migrate them to new API or explicitly archive them; do not leave broken active modules or unchanged Java production code while claiming a complete Kotlin rewrite.

## Next work / clarifications

No user clarification is required to choose the architecture. Treat this as a major-version source-breaking rewrite and explain migration. Exact newest dependency versions require primary-source online research by orchestrator. Decide sample/platform modernization consistently with the parent implementation plan.
