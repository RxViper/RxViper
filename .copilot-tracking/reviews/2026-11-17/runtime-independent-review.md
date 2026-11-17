<!-- markdownlint-disable-file -->
# Independent runtime and build review

Date: 2026-11-17. Reviewer: RPI Phase Implementor acting as independent reviewer.
Scope: `rxviper-core`, `rxviper-rxjava`, `rxviper-rxjava2`, `rxviper-rxjava3`,
`rxviper-coroutines`, root build, tests and generated publication POMs.

## Findings and resolution

1. **P1 — Android API 23–25 proxy Object methods could fail.**
   `rxviper-core/src/main/kotlin/com/dzaitsev/rxviper/internal/CallbackAttachment.kt`
   originally used `Method.parameterCount` in proxy `hashCode`/`toString` handling
   and validation. Android introduced `Method.getParameterCount()` in API 26,
   while the sample declares minSdk 23. Root replaced these calls with
   `parameterTypes.isEmpty()`, available since API 1. **Resolved.**
   Evidence: [official Android Method reference](https://developer.android.com/reference/java/lang/reflect/Method.html#getParameterCount()).

2. **P1 — Reentrant Rx clear could miss an in-progress synchronous subscription.**
   All three backend `Interactor.execute` implementations originally registered
   an empty serial handle, subscribed, then installed the resulting native handle.
   Clearing from the source factory or first synchronous value could therefore
   leave active work running until `subscribe` returned. Root now registers the
   native Subscriber/DisposableSubscriber before subscription and uses native safe
   subscriber wrapping for callback exceptions. Added two tests per backend for
   clear within the source factory and first synchronous callback, checking values,
   source side effects, completion and continued interactor reusability.
   **Resolved; six focused regressions pass.**

3. **P2 — Flow cancellation could still invoke completion.**
   `rxviper-coroutines/src/main/kotlin/com/dzaitsev/rxviper/coroutines/FlowInteractor.kt`
   called `onComplete` after collection even if the last `onNext` synchronously
   cancelled its Job. With matching caller/worker dispatchers, collection can
   finish without another suspension or cancellation check. Added a deterministic
   test that reproduced completion count 1 instead of 0. Root added
   `currentCoroutineContext().ensureActive()` before completion.
   **Resolved; regression and full coroutine tests pass.**

4. **P2 — Reattaching the original target could be undone by stale drop cleanup.**
   `CallbackAttachment.drop` originally checked only `current() === target` in
   its finally block. A hook that attached B then reattached original A created
   a new attachment that outer drop(A) incorrectly cleared. Root changed cleanup
   to compare the original WeakReference identity, preserving new attachment
   generations. Added separate A → B → A regressions for views and routers.
   **Resolved; core tests pass.**

## Architecture and lifecycle assessment

- Core depends only on Kotlin stdlib. Presenters, routers, weak null-object
  proxies and mapping live in the correct layer, with explicit class tokens
  avoiding fragile generic type reflection.
- Stable proxy identity, Object-method handling, Unit-only validation and
  unwrapped callback exceptions are tested. Targets are weakly referenced;
  strong targets in lifecycle hooks are limited to the hook stack. Single UI
  thread ownership is explicit, so unsynchronized reference access is consistent
  with the declared contract.
- Reusable cancellation and terminal close are separated. Native backend handles
  are registered before execution, completed work is removed from containers,
  and direct source invocation has documented caller-owned subscriptions.
- Backpressure stays native in Flowable. No artificial common reactive type or
  Android scheduler is introduced in core.
- Coroutine work belongs to supplied scopes; source dispatchers are injected.
  Cancellation is not routed as an ordinary source error. Flow catch placement
  preserves downstream callback failure propagation. Suspend success callbacks
  execute outside source-error handling.
- Generated POMs confirm publication isolation: core has only stdlib; each Rx
  artifact exposes core plus its own Rx generation and stdlib; the coroutine
  artifact exposes core, coroutines-core-jvm and stdlib. Test dependencies do not
  leak into runtime publications.
- Kotlin source targeting JVM 8 preserves the runtime libraries' bytecode level;
  the Gradle plugin independently targets JVM 17. Current tests ran on the
  installed JDK 21. Android device execution is outside this independent review.

## Validation performed

- `./gradlew :rxviper-rxjava:test :rxviper-rxjava2:test :rxviper-rxjava3:test
  --tests '*clear inside*' --stacktrace`: PASS after native-subscriber fix.
- `./gradlew :rxviper-coroutines:test --tests '*cancelling from last flow callback*'
  --stacktrace`: initially FAILED, confirming finding 3 before the fix.
- `./gradlew :rxviper-core:test :rxviper-coroutines:test --stacktrace`: PASS after
  fixes, including 14 core tests and 11 coroutine tests.
- Inspected generated `build/publications/mavenJava/pom-default.xml` in every
  runtime module to verify backend isolation and compile dependency scopes.

## Follow-up coverage suggestions

- Run the Android sample on API 23 and a current emulator to complement JVM tests
  and static API compatibility review.
- Add focused backend onNext/onError/onComplete callback-exception tests if the
  native safe-subscriber wrapping changes in future work.
- An optional deterministic cross-thread execute/clear/dispose stress test could
  complement the reentrant cancellation regressions; native subscription classes
  currently own thread safety and cancellation races.

Final independent review status: **Complete — all actionable findings resolved;
no remaining blocking runtime or publication-scope defect found.** Root owns final
full-build validation and the user-request fulfillment review.
