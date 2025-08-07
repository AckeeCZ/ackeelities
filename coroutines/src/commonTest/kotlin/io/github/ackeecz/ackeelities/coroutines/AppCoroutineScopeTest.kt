package io.github.ackeecz.ackeelities.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.ContinuationInterceptor

class AppCoroutineScopeTest : FunSpec({

    test("has SupervisorJob by default") {
        val underTest = AppCoroutineScope()
        val infiniteJob = underTest.launch {
            // suspend infinitely
            suspendCancellableCoroutine {}
        }

        underTest.launch { throw IllegalStateException() }.join()

        infiniteJob.isActive.shouldBeTrue()
        infiniteJob.cancel()
    }

    test("use custom-provided coroutine context") {
        val underTest = AppCoroutineScope(Dispatchers.Main)

        underTest.coroutineContext[ContinuationInterceptor]
            .shouldBeInstanceOf<MainCoroutineDispatcher>()
    }
})
