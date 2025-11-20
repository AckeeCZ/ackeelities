# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### core
### coroutines

## BOM [1.1.0] - 2025-11-20

### coroutines
#### Added
- New `async()` method in [SingleCoroutineLauncher], that launches a coroutine and returns a `Deferred<T>`
- New `cancel()` method in [SingleCoroutineLauncher], that cancels the job started by previous `launch()` or `async()` call

## BOM [1.0.1] - 2025-08-13

### coroutines
#### Fixed
- Missing dependency on coroutines artifact in the BOM.



## BOM [1.0.0] - 2025-08-13

### core
#### Added
- First version of the artifact 🎉

### coroutines
#### Added
- First version of the artifact 🎉
