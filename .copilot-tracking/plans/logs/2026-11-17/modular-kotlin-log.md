<!-- markdownlint-disable-file -->
# Planning log
Selected separate native backend APIs and pure core. New release is intentionally source/binary breaking; no compatibility facade that silently changes Rx major version.
Original checkout clean. Network DNS restricted in sandbox; initial curl failed, escalation succeeded. Dependency/tool downloads and Gradle execution may require sandbox escalation.
Gradle9.9.1 exceeds Kotlin2.4.21 fully tested range9.7.0: honor latest stable requirement and validate actual integration, record any incompatibility.
Follow-ups will be selected after final review.

## Implementation and validation iterations
Initial core/Rx/generator builds passed. Coroutine test compilation required explicit generic list types; coroutine stacktrace recovery copies standard exceptions, so tests assert type/message/causal origin instead of reference identity alone.
Android built-in Kotlin initially failed with BaseVariant classloader error: root resolves AGP alongside KGP with apply false, fixing plugin visibility. SDK platform37 finalized locally (PreviewSdkInt0); sample upgraded37 and lint passes.
Independent review found four issues, all corrected with regressions: use API1 parameterTypes instead of API26 Method.parameterCount; attach Rx native subscriber to composite before subscribing to close synchronous clear gap; check coroutine cancellation before Flow completion; compare weak-reference generations during stale drop cleanup so A->B->A reattachment survives.
No Java source remains; replaced rxviper runtime removed rather than leaving a stale unbuilt legacy tree. All explicit backend versions centralized. Native Android UI reduces obsolete sample dependencies and stays focused on VIPER rather than a UI framework migration.
Local Maven repository validates dependency isolation and plugin marker. Javadoc artifacts are empty Java-tool placeholders for Kotlin-only code; source/KDoc and README are included, full Dokka API website remains release follow-up.
The HVE git command references get_changed_files, which this runtime does not expose. Use already-reviewed files/build results and explicit path staging with standard git add/commit as the supported fallback. Do not stage unrelated .vscode user settings or build outputs. No remote push or issue mutation authorized/performed.

Final integrated validation passed:151 tasks,73 tests, no failures and Android lint clean. Review complete; follow-ups captured in final review document. Commit only task changes and tracking artifacts; keep unrelated editor settings untracked.
