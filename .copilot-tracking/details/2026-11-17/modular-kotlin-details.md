<!-- markdownlint-disable-file -->
# Execution details
Context: plans/2026-11-17/modular-kotlin-plan.instructions.md and research/2026-11-17/modular-kotlin-research.md.
1. Replace root Groovy configuration with Kotlin DSL and centralized catalog, current checksummed wrapper. Library target JVM8, build JDK17+; plugin JVM17. Maven local build repository for smoke publication.
2. Add shared presenters/weak no-op callbacks and Mapper in core. Explicit class tokens eliminate kotlin-reflect and inference fragility. Test stale lifecycle operations and proxy exception behavior.
3. Add backend-specific Interactor<Request,Response> classes. Defer source construction to subscription. Return native handles and use native composite containers for terminal disposal and reusable clearing. Verify scheduling, error routing, independent cancellation and reuse.
4. SuspendInteractor dispatches work with withContext; FlowInteractor defers creation and flowOn; invoke participates in caller lifetime. execute is convenience scoped launch, rethrows cancellation. Test virtual scheduling and parent cancellation without sleeps.
5. Generator templates reference each backend explicitly. Test generated source compiles. Sample retains cheese list/details using native Android widgets and coroutine main scope.
6. Validate ./gradlew check, publication smoke and Android build separately. Document all actual failures and fixes in planning log; final review audits explicit requests.
