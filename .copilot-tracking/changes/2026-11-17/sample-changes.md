<!-- markdownlint-disable-file -->
# Android sample migration — 2026-11-17

## Scope and changes

- Replaced every Java source in `sample/src/main/java` with Kotlin under
  `sample/src/main/kotlin`; preserved the cheese browser's list/details purpose
  and existing cheese catalog string arrays.
- Replaced `sample/build.gradle` with Kotlin DSL, AGP 9.5.1 from the shared
  catalog, Java 17 bytecode, minSdk 23, compileSdk/targetSdk 37, and dependencies
  on `rxviper-core`, `rxviper-coroutines`, and coroutines-android 1.11.0.
- Removed obsolete Dagger, RxAndroid, Android support widgets, Android APT,
  Retrolambda, data binding, and generated Java scaffolding dependencies.
- Added native Android Activity list/details UI and modern manifest.
- Constructed dependencies manually at the Activity edge. The presenter uses
  an injected suspend interactor and caller-owned coroutine scope.
- Attach view/router in `onStart`; detach in `onStop` and cancel the active
  request. Cancel the Activity-owned scope in `onDestroy`. Returning replays
  cached data or reloads; reload replaces an active request.
- Added five presenter tests and `sample/README.md`.

## SDK selection

The initial implementation used API 36 conservatively. Investigation confirmed
the installed API 37.0 SDK is finalized (`PreviewSdkInt=0`, empty codename),
and the official setup documentation uses integer compileSdk/targetSdk 37:
https://developer.android.com/about/versions/17/setup-sdk

The final sample uses API 37 (Android 17), supported by AGP 9.5.1, while keeping
minSdk 23. API 37.1-specific behavior is not needed by this sample.

## Build issue and fix

First AGP application failed with `NoClassDefFoundError: BaseVariant` because
KGP was loaded in the root classloader while AGP was declared only in the
sample. The root implementor declared the Android plugin with `apply false`
alongside KGP. The sample uses AGP's built-in Kotlin and needs no separate
Kotlin Android plugin.

## Validation

- `./gradlew -PandroidSample=true :sample:assembleDebug :sample:testDebugUnitTest :sample:lintDebug`
  passed on the final API 37 state (13 seconds, 54 actionable tasks).
- JUnit XML: 5 tests, 0 skipped, 0 failures, 0 errors. Tests cover cache replay,
  detach cancellation and reattachment, repository failure handling, replacing
  an active request, and router delegation.
- `git diff --check -- sample` passed.
- Debug APK: `sample/build/outputs/apk/debug/sample-debug.apk`.
- Android lint: no issues found. Removed five unused legacy resource warnings
  by deleting obsolete colors/string and applying existing spacing dimensions
  to the new layout.

No emulator/device interaction was performed; UI behavior is supported by
successful compilation, resource processing, unit tests, and static review.
