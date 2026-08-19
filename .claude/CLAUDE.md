# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ackeelities is a published **Kotlin Multiplatform (KMP) utility library** by Ackee. Targets are
Android (via the AGP KMP plugin `com.android.kotlin.multiplatform.library`) and iOS (iosArm64,
iosSimulatorArm64).

- Root package: `io.github.ackeecz.ackeelities`
- Maven coordinates: `io.github.ackeecz:ackeelities-*` (see `lib.properties`)
- Build tooling: convention plugins in `build-logic/`, Gradle Version Catalog (`gradle/libs.versions.toml`)
- **Public API is the deliverable** — every dump under `<module>/api/` is part of the contract (see API / ABI Validation).

## Module Structure

```
:core          — General-purpose utilities built only on regular Kotlin/Android APIs; no third-party library dependencies belong here. Published.
:coroutines    — Coroutine utilities (AppCoroutineScope, EventFlow, SingleCoroutineLauncher, TryCoroutine). Published.
:bom           — BOM (ackeelities-bom); pins a compatible set of artifact versions. Published.
:app           — Android sample app. NOT published; its unit tests run against artifacts published to Maven local during prePublishCheck to test the real published artifacts.
build-logic/   — Included build: convention plugins + release/verification tasks. Has its own test suite.
```

Module `build.gradle.kts` files are thin — most Gradle configuration lives in `build-logic`.

## Build & Compiler Configuration

- SDK levels and the JVM/Java target are single-sourced in `build-logic/.../util/Constants.kt` —
  read them there, never assume. `NAMESPACE_PREFIX` there also feeds each module's Android namespace.
- `allWarningsAsErrors = true` — even a deprecation warning fails the build.
- **Explicit API mode** is on for library modules (`explicitApi()` in `KmpLibraryPlugin`;
  `build-logic` uses `-Xexplicit-api=strict`) — public declarations need explicit visibility and
  return types.
- KMP source sets live under `src/<sourceSet>/kotlin` (`commonMain`, `androidMain`,
  `androidHostTest`, `commonTest`, ...); the `:app` sample keeps sources under `src/main/java`.

## API / ABI Validation

Public API is tracked with the **Kotlin Gradle Plugin's built-in ABI validation** (`abiValidation`,
enabled in `KmpLibraryPlugin`), using its **legacy dump** format.

- Dumps live at `<module>/api/<module>.api` (Android/JVM) **and** `<module>/api/<module>.klib.api`
  (klib validation for iOS targets) and are committed. Any change to a `public` declaration must be
  reflected there.

Workflow when public API changes:

1. `./gradlew updateLegacyAbi` to regenerate dumps.
2. Commit the dumps with the code change.

`checkLegacyAbi` fails the build (and `preMergeRequestCheck` / CI) when the committed dump is stale.
If the dump didn't move, the change wasn't public.

## Convention Plugins (`build-logic/`)

| Plugin ID | Applied to | Purpose |
|---|---|---|
| `ackeecz.ackeelities.kmp.library` | core, coroutines | KMP + Android KMP library, explicit API, ABI validation, Detekt over all KMP source sets, iOS frameworks |
| `ackeecz.ackeelities.kmp.testing` | KMP modules with tests | Kotest multiplatform + JUnit Platform, enables Android host tests |
| `ackeecz.ackeelities.android.application` | app | Android app conventions |
| `ackeecz.ackeelities.publishing` | published modules + bom | vanniktech Maven publish + Dokka + release/verify tasks; reads coordinates/versions from `lib.properties` |
| `ackeecz.ackeelities.preflightchecks` | root | Registers `preMergeRequestCheck` / `prePublishCheck` |

## Testing

- **Kotest `FunSpec` everywhere** — library modules and the build-logic suite. JUnit 5 platform is
  the runner.
- KMP tests live in `commonTest` and run as **Android host (local unit) tests**:

```bash
./gradlew testAndroidHostTest                 # all modules
./gradlew :coroutines:testAndroidHostTest     # single module
# Single test class (--tests filtering works on the JUnit Platform):
./gradlew :coroutines:testAndroidHostTest --tests "io.github.ackeecz.ackeelities.coroutines.SingleCoroutineLauncherTest"
```

- **iOS tests (`iosSimulatorArm64Test`) are currently disabled** in `preMergeRequestCheck` and CI
  due to a Kotlin/Kotest incompatibility (see the TODOs in `RegisterPreflightChecksPlugin` and
  `basic-preflight-check/action.yml`).
- **`build-logic` tests** (`./gradlew build-logic:logic:test`) are the model for unit tests: Kotest
  `FunSpec`, `withData` for data-driven cases, **hand-written test doubles only** (suffix `*Stub`) —
  no MockK; project fixtures via `buildProject()` / `Factories` under
  `build-logic/logic/src/test/.../testutil`.

## Verification & Publishing

**Before every PR**, run the preflight gate (see `CONTRIBUTING.md`):

```
./gradlew preMergeRequestCheck
```

It runs, across the relevant projects: `detekt` + assemble (`assembleAndroidMain`,
`compileKotlinIosSimulatorArm64`) + `testAndroidHostTest` + `checkLegacyAbi` + build-logic `:test`.
It is kept **in sync with CI** (`.github/actions/basic-preflight-check`, `.github/workflows/`) — if
you change what it runs, update CI too (the task source comments say so explicitly). The same
applies to `prePublishCheck` and `deploy.yml`.

Release procedure lives in `RELEASING.md`. Mental model:

- Every artifact has an independent version in `lib.properties` (`BOM_VERSION`, `CORE_VERSION`,
  `COROUTINES_VERSION`); the BOM pins a compatible set. Versions live there, not in Gradle files.
- Releases are driven by pushing a git tag `bom-<BOM_VERSION>` (`deploy.yml` triggers on `bom-*`).
- `checkIfUpdateNeededSinceCurrentTag` — lists artifacts changed since the last tag.
- `verifyPublishing` — fails if an internal-only change forces co-releasing dependents (protects
  binary compatibility between artifacts linked against the same internal code).
- `verifyBomVersion` — fails if the BOM version doesn't match the pushed git tag.
- `prePublishCheck` — `preMergeRequestCheck` + `verifyPublishing` + `verifyBomVersion` + publishes
  to Maven local and runs `:app:testDebugUnitTest` against the published artifacts; run before
  pushing a tag (synced with `deploy.yml`).
- Publishing (vanniktech + Dokka) **probes Maven Central (`repo1.maven.org`) first**: 404 → publish,
  2xx → skip (already published), any other status → fail. Re-pushing the same tag publishes nothing;
  to re-publish, bump the version — never force. Signing (GPG) credentials are provided by CI secrets.
- Adding a new publishable module requires: an entry in `settings.gradle.kts`, properties in
  `lib.properties`, a subclass + mapping in `build-logic`'s `ArtifactProperties`, a
  `PublishableProject` entry, and a constraint in `bom/build.gradle.kts`.

New changes need tests and an entry in the `Unreleased` section of `CHANGELOG.md` (Keep a Changelog
format, grouped per module).

### Android/KMP Verification Tasks

- Assemble: `assembleAndroidMain`
- Detekt: `detekt`
- Unit tests: `testAndroidHostTest`

## Code Style

- Blank line after a type-body opening brace, before the first member — applies to `class`,
  `interface`, `sealed interface`, `object` / `data object`, `enum class`. **Does not** apply to
  lambda/DSL bodies (e.g. `FunSpec({ … })`, `apply { }`).
- Max line length **150** (Detekt, `detekt-config.yml`, run with `detekt-formatting`; run via
  `./gradlew detekt`). Some rules are relaxed there (`TooManyFunctions`,
  `TooGenericExceptionCaught` off).
- No wildcard imports.
- Idioms in use: `internal` for impl types (on top of explicit API mode), KDoc on public
  declarations.

## Plans

At the end of each plan, give me a list of unresolved questions to answer, if any. Make the questions
extremely concise. Sacrifice grammar for the sake of concision. Use the AskUserQuestion tool.
