<!-- markdownlint-disable-file -->
# Modular Kotlin rewrite review
Date2026-11-17. Reviewer:root plus independent runtime review subagent.
Plan: ../../plans/2026-11-17/modular-kotlin-plan.instructions.md
Status: Complete. Two implementation/review iterations.

## User request fulfillment
- Complete: RPI workflow research, plan, implementation, review and discovery. Durable artifacts in .copilot-tracking.
- Complete: Kotlin-only runtime/core/adapters/plugin/sample/tests. No .java files remain outside generated build/cache outputs.
- Complete: rxviper-core contains shared presenter/router/view/mapper abstractions, no Android/Rx/coroutine dependency.
- Complete: separate RxJava1/2/3 modules with native subscription/disposal and Rx2/3 backpressure; separate coroutine module with suspend and Flow contracts.
- Complete: central latest stable direct dependencies/Gradle; Kotlin DSL build and checksummed official wrapper9.9.1.
- Complete: generator and Android sample migrated rather than left broken on old APIs.
- Complete: unreleased2.0 changelog/migration/documentation reference issue21,43,52 and related40. Issue53/54/58 not claimed resolved.
- User follow-up commit: prepared descriptive conventional message and explicit staging scope; commit follows this review.

## Validation evidence
- Final command: ./gradlew -PandroidSample=true check assemble publishAllPublicationsToBuildRepository :sample:testDebugUnitTest :sample:lintDebug --console=plain
- BUILD SUCCESSFUL in13s;151 tasks (69 executed,8 cached,74 up-to-date). Includes JVM checks, debug/release Android assembly, plugin validation, all local Maven publications and marker.
-73 tests total: core14, Rx1 12, Rx2 13, Rx3 13, coroutines11, plugin5, Android presenter5. Zero failures/errors/skips.
- Plugin compiler test covers all4 generated backend variants; TestKit verifies task/configuration cache reuse, up-to-date detection and stale-source cleanup.
- Android lint report: No issues found. Debug APK built; UI behavior not exercised on emulator/device in this session.
- Core runtimeClasspath: kotlin-stdlib2.4.21 and its transitive annotations13.0 only. Publication POMs show each adapter core+own backend+stdlib, no cross-backend leakage. Plugin POM has only stdlib.
- Jar classfile versions verified: runtime52 (Java8), plugin61 (Java17).
- Official wrapper jar checksum621008afc4f5d59357fcf06ffa644dd5aade5e091bdc363742710445f73cdaf8 matches release metadata; distribution ZIP checksum configured.
- Authored source/text inspected; legacy trailing whitespace removed from changelog.

## Findings resolved
See runtime-independent-review.md: API26 reflection replaced; Rx native subscriber registered before subscription; Flow cancellation checkpoint before completion; attachment generation check prevents stale drop after same-object reattach. Regression tests pass.
Coroutine error tests account for native stacktrace-recovery exception copying, preserving failure type/message/causal origin. Source errors and callback errors distinguished.
AGP/Kotlin classpath visibility fixed by root apply-false declaration. Kotlin2.4.21/Gradle9.9.1 integration validated despite exceeding fully tested range in Kotlin docs.

## Practical limits
Local2.0.0-SNAPSHOT only. No signed remote release, push, issue closure/comment or remote CI execution. IDE template integration and hook-parameter removal remain distinct backlog requests. Kotlin consumer metadata baseline is2.4.

## Phase5 Discover: prioritized follow-ups
1. Prepare signed release publication to Maven Central and Gradle Plugin Portal, plus generated Kotlin API documentation. Requires release/account configuration and deliberate release approval.
2. Decide issue58 hook-parameter removal and freeze v2 API with a binary compatibility baseline before publishing. Current hooks retained and proxy usage documented.
3. Add Android Studio IDE templates if still desired (issue53); current Gradle scaffolding addresses52 without IDE installation.
