package io.github.ackeecz.ackeelities.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException

private lateinit var underTest: SingleCoroutineLauncher

internal class SingleCoroutineLauncherTest : FunSpec({

    beforeEach {
        underTest = SingleCoroutineLauncher(CoroutineScope(UnconfinedTestDispatcher()))
    }

    suspend fun suspendCoroutine() = suspendCancellableCoroutine<Unit> {}

    test("new launch cancels previous launch") {
        var wasPreviousLaunchCancelled = false
        underTest.launch {
            try {
                suspendCoroutine()
            } catch (e: CancellationException) {
                wasPreviousLaunchCancelled = true
                throw e
            }
        }

        underTest.launch {}

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("new launchIn cancels previous launchIn") {
        var wasPreviousLaunchCancelled = false
        flow<Unit> {
            try {
                suspendCoroutine()
            } catch (e: CancellationException) {
                wasPreviousLaunchCancelled = true
                throw e
            }
        }.launchIn(underTest)

        flow<Unit> {}.launchIn(underTest)

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("cancel function cancels job") {
        var wasPreviousLaunchCancelled = false
        underTest.launch {
            try {
                suspendCoroutine()
            } catch (e: CancellationException) {
                wasPreviousLaunchCancelled = true
                throw e
            }
        }

        underTest.cancel()

        wasPreviousLaunchCancelled.shouldBeTrue()
    }

    test("async cancellation is correctly handled") {
        runTest {
            val single = SingleCoroutineLauncher(this)
            val async = single.async {}

            single.cancel()
            shouldThrow<CancellationException> { async.await() }
        }
    }
})
