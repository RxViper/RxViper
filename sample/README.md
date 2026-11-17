# Android sample

The original cheese browser is rewritten in Kotlin using `rxviper-core` and
`rxviper-coroutines`. A native Android Activity implements view and router
interfaces, the presenter owns display logic, and a suspend interactor loads
the data. Dependencies are constructed directly in the Activity.

The sample is optional so building the library requires no Android SDK:

```shell
./gradlew -PandroidSample=true :sample:assembleDebug :sample:testDebugUnitTest
```

Install Android SDK platform 37 (Android 17) and configure `ANDROID_HOME` or `sdk.dir` in
`local.properties`. Run the resulting APK on Android 6.0/API 23 or later.

The Activity attaches its view and router in `onStart`, detaches them in
`onStop`, and cancels its coroutine scope in `onDestroy`. Detaching cancels
the presenter's current request; returning to the screen replays cached data
or starts another request. Reload replaces the current request. Cancellation
is propagated and is never displayed as a loading error.

The sample uses platform widgets to keep attention on VIPER and coroutine
ownership. The library also works with AndroidX, Compose, or other JVM UIs;
those integrations belong to the consuming application.
