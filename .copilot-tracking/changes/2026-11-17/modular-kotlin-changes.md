<!-- markdownlint-disable-file -->
# Modular Kotlin rewrite changes
Plan: ../../plans/2026-11-17/modular-kotlin-plan.instructions.md
Date:2026-11-17. Iterations:2 (initial implementation and review-driven corrections).

## Added
- rxviper-core/: Kotlin shared lifecycle, weak no-op proxy, markers, Mapper and14 tests.
- rxviper-rxjava/, rxviper-rxjava2/, rxviper-rxjava3/: scheduler-aware native use cases and38 tests.
- rxviper-coroutines/: suspend and Flow use cases with structured lifetime and11 tests.
- build.gradle.kts, settings.gradle.kts, gradle/libs.versions.toml: current centralized build and six Maven publications plus marker.
- MIGRATION.md and module READMEs: usage, architecture and breaking migration guidance.
- .github/workflows/build.yml: JVM and Android CI validation (workflow not executed remotely in this session).

## Modified
- rxviper-gradle-plugin/: replace JavaPoet codegen with Kotlin per-backend templates; cacheable managed generation;5 tests incl four compiler variants/TestKit.
- sample/: all Kotlin cheese list/details Android app, cancellation and5 tests; SDK37 AGP9.5.1, zero lint issues.
- gradle wrapper jar/scripts/properties:9.9.1 official wrapper, jar and ZIP SHA256 verified.
- gradle.properties, .gitignore: modern build flags and nested output ignores.
- README.md, CHANGELOG.md: complete docs/unreleased2.0 entry, issue mapping21/43/52 and related40.

## Removed
- rxviper/ legacy Java runtime/tests and old per-module properties.
- Root build.gradle, settings.gradle, dependencies.gradle, license.gradle, publish.gradle.
- Java sample, Dagger/support/RxAndroid/Retrolambda/APT/Bintray/JCenter dependencies and old Java generator tests/templates.

## Release summary
Breaking local snapshot2.0.0-SNAPSHOT. Five runtime artifacts plus independent generator, each backend exports core and only its own async dependency. Runtime jars JVM8; build/plugin JDK17+. No public release or push performed.
