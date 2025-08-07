package io.github.ackeecz.ackeelities.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.cancellation.CancellationException

internal class TryCoroutineTest : FunSpec({

    test("assign result from try when successful") {
        val expected = "result"
        var actual = ""

        tryCoroutine { actual = expected } catch {}

        actual shouldBe expected
    }

    test("catch thrown exception and assign it") {
        val expected = Exception("tryCoroutine-catch")
        var actual: Throwable? = null

        tryCoroutine { throw expected } catch { actual = it }

        actual shouldBe expected
    }

    test("tryCoroutine-catch catches cancellation exception if not caused by coroutine cancellation") {
        val expected = CancellationException()
        var actual: Throwable? = null

        tryCoroutine { throw expected } catch { actual = it }

        actual shouldBe expected
    }

    test("tryCoroutine-catch doesn't catch cancellation exception if caused by coroutine cancellation") {
        launch(UnconfinedTestDispatcher()) {
            var actual: Throwable? = null

            shouldThrow<CancellationException> {
                tryCoroutine { suspendCancellableCoroutine<Unit> {} } catch { actual = it }
            }

            actual.shouldBeNull()
        }.cancel()
    }

    test("return result from try when successful") {
        val expected = "result"

        val actual = tryCoroutine { expected } catch { "fallback" }

        actual shouldBe expected
    }

    test("return result from catch when exception is thrown") {
        val expected = "result"

        val actual = tryCoroutine { throw IllegalStateException() } catch { expected }

        actual shouldBe expected
    }

    test("rethrow exception rethrown from catch") {
        shouldThrow<IllegalStateException> {
            tryCoroutine { throw IllegalStateException() } catch { throw it }
        }
    }

    test("can run suspend fun in tryCoroutine block") {
        val expected = "result"

        val actual = tryCoroutine { longSuspendingFun(expected) } catch { "fallback" }

        actual shouldBe expected
    }

    test("can run suspend fun in catch block") {
        val expected = "result"

        val actual = tryCoroutine { throw IllegalStateException() } catch { longSuspendingFun(expected) }

        actual shouldBe expected
    }
})

private suspend fun longSuspendingFun(returnValue: String): String {
    delay(1)
    return returnValue
}
