# Kotlin scaffolding plugin

The `com.dzaitsev.rxviper` plugin generates editable Kotlin starter code for a
screen's view callbacks, presenter, optional router and interactors. It has no
runtime dependency on RxViper or any async backend. Add the selected RxViper
backend to the application separately.

For the local snapshot, first run `./gradlew publishAllPublicationsToBuildRepository`
in this repository, then add the build repository to the consuming project's
`settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        maven { url = uri("/path/to/RxViper/build/repository") }
        gradlePluginPortal()
        mavenCentral()
    }
}
```

The plugin's build-script configuration is:

```kotlin
import com.dzaitsev.rxviper.plugin.Backend

plugins {
    id("com.dzaitsev.rxviper") version "2.0.0-SNAPSHOT"
}

rxViper {
    packageName.set("example.app")
    backend.set(Backend.COROUTINES) // or RXJAVA, RXJAVA2, RXJAVA3
    screens {
        create("home") {
            routesTo.set(listOf("Details", "Settings"))
            interactors {
                create("loadItems") {
                    requestType.set("kotlin.Unit")
                    responseType.set("kotlin.String")
                }
            }
        }
    }
}
```

Run `./gradlew generateRxViper`, then copy the generated files from
`build/generated/rxviper/kotlin` into `src/main/kotlin` and implement the business
logic. Source sets are deliberately not connected automatically: generated code
is a starter template that needs editing. Re-running the task replaces its
entire output directory, including removal of obsolete files. Never point
`outputDirectory` at a directory containing handwritten files.

The default backend is coroutines; each generated coroutine presenter accepts a
caller-owned scope and core `SuspendInteractor` contracts, allowing interactor or
lambda implementations. Generated files import their backend's `execute`
extension. Rx presenters keep concrete interactor parameters because they manage
those interactors with `clear()`. Interactors receive an explicit dispatcher or pair of Rx
schedulers. Generated presenters cancel their work when a view is dropped. The
scope's dispatcher must be appropriate for view callbacks (usually Main on
Android). The plugin does not add Android or a Main dispatcher dependency.

Migration from the old DSL:

- Rename the old `useCases { ... }` block to `interactors { ... }`.

- Configuration values use Gradle `Property.set(...)` and `ListProperty.set(...)`.
- `requestClass` and `responseClass` become fully qualified Kotlin `requestType`
  and `responseType` strings. Only simple qualified type names are supported;
  introduce a domain wrapper for generic types. Defaults are `kotlin.Unit` and
  `kotlin.Any`.
- Each screen has its own package under `packageName`. `splitPackages`,
  `useLambdas` and `addMetaInfo` are removed; output is always Kotlin and
  deterministic, with no timestamps.
- `destinationDir` becomes `outputDirectory`; the default is under `build`,
  rather than writing into application source directories.
- `includeRouter` and `includeInteractor` are configured per screen.
- An empty use case container produces one interactor named after the screen.
- All generated source is compiled against the real core and backend APIs in
  the plugin's tests. TestKit verifies configuration cache reuse and stale-file
  cleanup.
