<!-- markdownlint-disable-file -->
# Rx backend test implementation

Date: 2026-11-17. Role: delegated RPI implementation and validation.

## Added

- `rxviper-rxjava/src/test/kotlin/com/dzaitsev/rxviper/rxjava/InteractorTest.kt`: 10 tests.
- `rxviper-rxjava2/src/test/kotlin/com/dzaitsev/rxviper/rxjava2/InteractorTest.kt`: 11 tests.
- `rxviper-rxjava3/src/test/kotlin/com/dzaitsev/rxviper/rxjava3/InteractorTest.kt`: 11 tests.

All three backends now verify cold source construction per subscription, explicit
request forwarding, source creation on the injected subscription scheduler,
delivery on the injected observer scheduler, construction and source failures,
synchronous completion and terminal execution handles, independent execution
cancellation, clear/reuse, terminal close and rejection of new managed work,
cancellation before scheduled construction, and direct invocation ownership.
RxJava 2/3 also verify request-driven backpressure through native Flowable types.

Tests use trampoline or virtual TestScheduler instances; no sleeps or real-thread
timing assumptions. Production implementations were not changed by this phase.

## Validation

Command:

```text
./gradlew :rxviper-rxjava:test :rxviper-rxjava2:test :rxviper-rxjava3:test --console=plain
```

Result: BUILD SUCCESSFUL in 4s. All 32 tests passed. Gradle 9.9.1 and Kotlin 2.4.21.
No implementation defects discovered by this test suite.
