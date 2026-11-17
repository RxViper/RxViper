<!-- markdownlint-disable-file -->
# Kotlin scaffolding plugin migration

Completed 2026-11-17 as the RPI Phase Implementor for the generator phase.

## Changes

- Replaced legacy JavaPoet-based Java generators and outdated tests with a pure
  Kotlin template generator. The plugin has no RxViper or backend dependency at
  runtime; backend modules are dependencies of its tests only.
- Added `Backend` selection for RxJava 1, RxJava 2, RxJava 3 and coroutines, with
  coroutines as default. Generated interactors use injected schedulers or a
  dispatcher; generated presenters use the corresponding current API and cancel
  work when views detach. Completed coroutine jobs are pruned on the next call.
- Replaced eager configuration with Gradle Property and named-container DSLs.
  Generated sources are lazy task inputs and the task accesses no Project state
  during execution. It is cacheable, deterministic and removes stale generated
  files with Gradle sync.
- Moved generated output into a task-owned build directory. Users copy starter
  files into source directories before editing; auto-compilation of regenerating
  templates would overwrite implementations or duplicate copied types.
- Added modern `java-gradle-plugin` registration and Kotlin build script. Removed
  the hand-maintained plugin descriptor, legacy module properties and JavaPoet.
- Documented new Kotlin DSL and all removed/replaced DSL options in
  `rxviper-gradle-plugin/README.md`.

## Validation

- `./gradlew :rxviper-gradle-plugin:test --stacktrace`: PASS, 5 tests. Latest
  passing run after completed-job pruning took 11 seconds.
- Tests compile generated code for every backend against the actual core and
  backend modules, including optional routing, omitted interactors and default
  Unit requests. Compiler test dependency uses the version catalog Kotlin version.
- Gradle TestKit verifies execution, configuration cache reuse, up-to-date
  detection and deletion of obsolete router files.
- Unit tests cover lazy DSL inputs, deterministic generation, backend isolation,
  input/path validation and generated-type collisions.
- `./gradlew :rxviper-gradle-plugin:validatePlugins
  :rxviper-gradle-plugin:publishAllPublicationsToBuildRepository --stacktrace`:
  PASS. Inspected the generated marker POM: plugin ID
  `com.dzaitsev.rxviper` resolves `com.dzaitsev.rxviper:rxviper-gradle-plugin`.

## Limits

- Fully qualified simple Kotlin request/response type names are supported;
  generic types should use a domain wrapper. This is documented and enforced.
- Generated interactors contain TODO/empty-source placeholders for application
  business logic. Generated coroutine presenters require a caller-owned scope
  whose dispatcher is appropriate for view callbacks.
- No remote publishing or Plugin Portal release was performed.
