# Ackeelities

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ackeecz/ackeelities-bom)](https://central.sonatype.com/artifact/io.github.ackeecz/ackeelities-bom)

## Overview

KMP library that provides a set of utilities for KMP projects.

## Architecture

Library consists of several modules:
- `core` contains general-purpose utilities that are not tied to a specific library. There should be just basic utilities
that are built on top of regular Kotlin APIs.
- `coroutines` contains utilities for coroutines, such as `AppCoroutineScope`.

## Setup

Add the following dependencies to your `libs.versions.toml`, depending on what you need. You should
always use BOM to be sure to get binary compatible dependencies.

```toml
[versions]
ackee-ackeelities-bom = "SPECIFY_VERSION"

[libraries]
ackee-ackeelities-bom = { module = "io.github.ackeecz:ackeelities-bom", version.ref = "ackee-ackeelities-bom" }
ackee-ackeelities-core = { module = "io.github.ackeecz:ackeelities-core" }
ackee-ackeelities-coroutines = { module = "io.github.ackeecz:ackeelities-coroutines" }
```

Then specify dependencies in your `build.gradle.kts`:

```kotlin
dependencies {

    // Always use BOM
    implementation(platform(libs.ackee.ackeelities.bom))
    implementation(libs.ackee.ackeelities.core)
    implementation(libs.ackee.ackeelities.coroutines)
}
```

## Credits

Developed by [Ackee](https://www.ackee.cz) team with 💙.
