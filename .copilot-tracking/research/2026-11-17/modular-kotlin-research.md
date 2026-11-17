<!-- markdownlint-disable-file -->
# Modular Kotlin rewrite research
Difficulty: challenging. RPI research and implementation use focused subagents; root coordinates build, backends, integration and review.

## Scope and success criteria
Rewrite Java runtime and Android sample in Kotlin; extract rxviper-core independent of Android/Rx/coroutines; add RxJava 1, 2, 3 and coroutines modules; modernize generator/build/dependencies; verify tests, publication metadata, generated Kotlin and Android assembly.

## Evidence
- Original build: Gradle 3.4.1, Kotlin 1.2.20, JCenter/Bintray, Android plugin 2.2.2. Current runtime already uses Rx2 Flowable 2.1.8.
- Local investigation: no repository AGENTS.md or .github instructions; initial git worktree clean; JDK21 and Android SDK36 installed.
- Architecture report: ../subagents/2026-11-17/architecture-research.md.
- https://services.gradle.org/versions/current: stable Gradle 9.9.1, SHA256 d8a18a7a841dd5df825d35a685fad6f2d7f12ac0c280e57b67c27b4a520ba228.
- https://kotlinlang.org/docs/gradle-configure-project.html: stable Kotlin2.4.21; tested Gradle range through9.7.0, newer Gradle supported with caveats. Integration tests required for9.9.1.
- Maven Central metadata (repo.maven.apache.org/maven2): Rx1 1.3.8; Rx2 2.2.21; Rx3 3.1.12; coroutines1.11.0; JUnit4.13.2. Kotlin2.5.0-RC excluded as prerelease.
- https://github.com/Kotlin/kotlinx.coroutines/releases:1.11.0 stable.
- https://developer.android.com/build/releases/agp-9-5-0-release-notes: AGP9.5.1 latest patch, JDK17+, Gradle9.6+.

## Selected design
Dependency arrows: each backend -> core; no cross-backend dependency. Core includes marker interfaces, presenters and Mapper. Keep no-op detached callbacks and weak references, replace generic-reflection inference with explicit class tokens. Native async return types avoid building a lowest-common-denominator abstraction. Kotlin stdlib is the only external core runtime dependency.
Rx1 Observable and Rx2/3 Flowable expose cold deferred sources plus managed execute callbacks; clear() reusable; unsubscribe()/dispose() terminal. Caller supplies schedulers; no Android scheduler dependency. Coroutines provide suspend and Flow variants with injected dispatcher and caller-owned scopes, preserving cancellation.
Retain and modernize scaffolding plugin to generate Kotlin for selected backend. Retain Android sample but opt in via -PandroidSample=true so SDK installation is unnecessary for JVM consumers.

## Alternatives
Nullable presenter references would be idiomatic but discard defining detached no-op semantics. Generic async core interactor would hide native backpressure/cancellation semantics without useful shared logic. Reimplementing presenters in every backend would duplicate lifecycle behavior. Retaining broken Java generator/sample contradicts full Kotlin migration.

## GitHub issue assessment (user follow-up)
Read https://api.github.com/repos/RxViper/RxViper/issues?state=open&per_page=100 and comments for21,40,52,54 on2026-11-17.
-43 Add RxJava2 support: complete separate tested/publishable Rx2 module. Closing reference warranted.
-21 Provide detailed README: complete module/install/lifecycle/Rx/coroutines/generator/sample/migration docs. Closing reference warranted.
-52 Auto Generate Viper classes: complete tested Kotlin generator and instructions; IDE template follow-up belongs separately to53. Closing reference warranted for class generation.
-40 Observe in main thread: related question; deferred worker construction + explicit observe scheduler and sample UI scope documented/tested. Reference only.
-53 Android IDE templates: not implemented; keep open.
-54 executeUnsafe: not implemented; explicit no-op error callback possible but no dedicated method, keep open.
-58 Remove hook arguments: not implemented; current hooks retained and asynchronous proxy access documented, keep open.
Additional user request: commit changes with descriptive issue references and update changelog.
