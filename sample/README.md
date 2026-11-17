# Compose Android sample

The cheese browser uses Jetpack Compose and Material 3 for both its list and
details screens, with `rxviper-core` presenters and coroutine interactors.
Dependencies are constructed once in `MainActivity`, outside composition.

The Activity implements `MainViewCallbacks` and translates presenter callbacks
into immutable `MainUiState` snapshots held in Compose state. `MainScreen` renders
that state and forwards reload and selection events to the presenter. The router
opens `DetailsActivity`, whose content is also Compose. Recomposition does not
recreate the presenter or start another request.

The presenter depends on core's `SuspendInteractor` contract and calls the
coroutine `execute` extension. Production supplies a dispatcher-aware interactor;
tests can supply a suspend lambda without inheriting a backend class. Compose
dependencies stay in the sample, so the runtime libraries remain UI-independent.

## Build configuration

The sample's Trellis declaration enables Compose:

```kotlin
androidApp("sample", "com.dzaitsev.rxviper.sample") { usesCompose = true }
```

Trellis applies the Compose compiler plugin and Android Compose settings. The
root build supplies the compiler plugin version with `apply false`; the sample
has no duplicate plugin declaration or manual Compose build feature. Dependencies
use the Compose BOM from the version catalog.

The sample is optional, so building the library requires no Android SDK:

```sh
./gradlew -PandroidSample=true :sample:assembleDebug :sample:testDebugUnitTest :sample:lintDebug
```

Install Android SDK platform 37 (Android 17) and configure `ANDROID_HOME` or
`sdk.dir` in `local.properties`. Run the resulting APK on Android 6.0/API 23 or
later. `MainScreen` and `DetailsScreen` include Android Studio previews. The
Material 3 theme follows system light/dark mode, with edge-to-edge screen insets.

## Lifecycle

The Activity attaches its view and router in `onStart`, detaches them in
`onStop`, and supplies its `lifecycleScope`, which AndroidX cancels on destruction.
Detaching cancels the
presenter's current request; returning to the screen replays cached data or starts
another request. Reload replaces the current request. Cancellation is propagated
and is never displayed as a loading error.

## Tests and release builds

`testDebugUnitTest` runs the presenter tests and Compose UI tests. Robolectric
runs the UI tests on an Android API 35 environment inside the host JVM; an
emulator is not required. The tests exercise list selection and reload, loading,
empty and error states, and the details/back action. These tests also run in the
existing Android CI job.

Release builds use Trellis's shrinking configuration. `proguard-rules.pro` retains
view and router interfaces invoked through RxViper's dynamic proxies. Build both
variants with `./gradlew -PandroidSample=true :sample:assemble`.
