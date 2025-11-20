package io.github.ackeecz.ackeelities.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.UnconfinedTestDispatcher

private lateinit var underTest: SingleCoroutineLauncher

internal class SingleCoroutineLauncherTest : FunSpec({

    beforeEach {
        underTest = SingleCoroutineLauncher(CoroutineScope(UnconfinedTestDispatcher()))
    }

    suspend fun suspendCoroutine(onCancellation: () -> Unit) = suspendCancellableCoroutine<Unit> { continuation ->
        continuation.invokeOnCancellation { onCancellation() }
    }

    test("new launch cancels job started by previous launch") {
        var wasPreviousLaunchCancelled = false
        underTest.launch {
            suspendCoroutine(
                onCancellation = {
                    wasPreviousLaunchCancelled = true
                }
            )
        }

        underTest.launch {}

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("new launchIn cancels job started by previous launchIn") {
        var wasPreviousLaunchCancelled = false
        flow<Unit> {
            suspendCoroutine(
                onCancellation = {
                    wasPreviousLaunchCancelled = true
                }
            )
        }.launchIn(underTest)

        flow<Unit> {}.launchIn(underTest)

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("cancel function cancels job started by launch") {
        var wasPreviousLaunchCancelled = false
        underTest.launch {
            suspendCoroutine(
                onCancellation = {
                    wasPreviousLaunchCancelled = true
                }
            )
        }

        underTest.cancel()

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("cancel function cancels job started by async") {
        var wasPreviousLaunchCancelled = false
        underTest.async {
            suspendCoroutine(
                onCancellation = {
                    wasPreviousLaunchCancelled = true
                }
            )
        }

        underTest.cancel()

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("new async cancels job started by previous async") {
        var wasPreviousLaunchCancelled = false
        underTest.async {
            suspendCoroutine(
                onCancellation = {
                    wasPreviousLaunchCancelled = true
                }
            )
        }

        underTest.async {}

        wasPreviousLaunchCancelled.shouldBeTrue()
    }
})
