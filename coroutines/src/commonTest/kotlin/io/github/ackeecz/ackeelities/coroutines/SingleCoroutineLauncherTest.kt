package io.github.ackeecz.ackeelities.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.cancellation.CancellationException

private lateinit var underTest: SingleCoroutineLauncher

internal class SingleCoroutineLauncherTest : FunSpec({

    beforeEach {
        underTest = SingleCoroutineLauncher(CoroutineScope(UnconfinedTestDispatcher()))
    }

    suspend fun suspendCoroutine() = suspendCancellableCoroutine<Unit> {}

    suspend fun trySuspendCoroutine(onCancellationException: () -> Unit) {
        try {
            suspendCoroutine()
        } catch (e: CancellationException) {
            onCancellationException()
            throw e
        }
    }

    test("new launch cancels job started by previous launch") {
        var wasPreviousLaunchCancelled = false
        underTest.launch {
            trySuspendCoroutine {
                wasPreviousLaunchCancelled = true
            }
        }

        underTest.launch {}

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("new launchIn cancels job started by previous launchIn") {
        var wasPreviousLaunchCancelled = false
        flow<Unit> {
            trySuspendCoroutine {
                wasPreviousLaunchCancelled = true
            }
        }.launchIn(underTest)

        flow<Unit> {}.launchIn(underTest)

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("cancel function cancels job started by launch") {
        var wasPreviousLaunchCancelled = false
        underTest.launch {
            trySuspendCoroutine {
                wasPreviousLaunchCancelled = true
            }
        }

        underTest.cancel()

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("cancel function cancels job started by async") {
        var wasPreviousLaunchCancelled = false
        underTest.async {
            trySuspendCoroutine {
                wasPreviousLaunchCancelled = true
            }
        }

        underTest.cancel()

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("new async cancels job started by previous async") {
        var wasPreviousLaunchCancelled = false
        underTest.async {
            trySuspendCoroutine {
                wasPreviousLaunchCancelled = true
            }
        }

        underTest.async {}

        wasPreviousLaunchCancelled.shouldBeTrue()
    }
})
