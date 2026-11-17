<!-- markdownlint-disable-file -->
# Modular Kotlin rewrite plan
## User Requests
Source: user request 2026-11-17. Follow-up: commit changes with descriptive message, reference resolved GitHub issues, update changelog. Use RPIAgent; modernize abandoned VIPER library; fully Kotlin; independent rxviper-core; separate RxJava1,2,3 and coroutines modules; latest Gradle and dependencies; convenient modular design.
## Context Summary
No repository instructions discovered. Existing code, README and architecture research inspected. Applies HVE hve-core-workflows skill and packaged RPI agent guidance.
## Objectives
Keep shared lifecycle in core and native async contracts in optional modules. Preserve detached callback behavior while correcting disposal and coroutine ownership. Keep generator/sample coherent with breaking v2 API.
## Implementation
- [x] Research architecture, installed toolchain, release metadata.
- [x] Build: Kotlin DSL, catalog, wrapper9.9.1, Kotlin2.4.21, modern Maven publication. <!-- parallelizable: false -->
- [x] Kotlin core + lifecycle/proxy/mapper tests (core subagent). <!-- parallelizable: true -->
- [x] Rx1/2/3 interactors + scheduler/error/cancellation tests (root). <!-- parallelizable: true -->
- [x] Suspend/Flow interactors + structured cancellation tests (root). <!-- parallelizable: true -->
- [x] Kotlin code generator + generated-source compilation tests (plugin subagent). <!-- parallelizable: true -->
- [x] Kotlin Android sample (sample subagent, depends on core/coroutines API). <!-- parallelizable: true -->
- [x] Remove replaced legacy runtime/build, write README/migration/changelog and CI.
- [x] Validate JVM check, generated compilation, core dependency isolation, publication and Android assembly; fix findings.
- [x] Review each request; record changes/review and discover follow-ups.
## Planning Log
logs/2026-11-17/modular-kotlin-log.md
## Dependencies
HVE Core skills/hve-core-workflows/SKILL.md; commands/hve-core/rpi.md; agents/hve-core/rpi-agent.md. JDK17+ (local21), Gradle network downloads, Android SDK36 for opt-in sample.
## Success Criteria
All source Kotlin; five runtime modules independently publishable; core lacks async dependencies; all tests/checks pass; plugin emits compiling Kotlin for each backend; Android sample assembles; documentation describes breaking changes and lifecycle ownership.
