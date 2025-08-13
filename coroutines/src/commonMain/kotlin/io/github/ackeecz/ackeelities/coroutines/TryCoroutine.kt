@file:Suppress("Filename")

package io.github.ackeecz.ackeelities.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/**
 * tryCoroutine/catch DSL enables to try/catch the exceptions thrown from the suspend functions
 * exactly the same like by using standard try/catch blocks with the difference that it does not
 * catch [CancellationException] and automatically rethrows it to the caller. Same as try/catch, it
 * can serve either as a statement or an expression.
 */
// suspend is intentional to force usage only in coroutines since normal functions should use classic try/catch.
// inline is intentional as well, since it allows stuff like using return statement both inside tryBlock and
// before tryCoroutine function call, so it allows for more flexible usage and code writing styles.
public suspend inline fun <T> tryCoroutine(tryBlock: () -> T): TryCoroutineResult<T> {
    return try {
        TryCoroutineResult.Success(tryBlock())
    } catch (e: Throwable) {
        // Checks if coroutine was cancelled or not and differentiates between CancellationException
        // thrown by coroutine cancellation, which should be rethrown, and CancellationException
        // thrown because of other reasons. This check throws CancellationException if coroutine
        // cancelled and otherwise just returns.
        coroutineContext.ensureActive()
        TryCoroutineResult.Error(e)
    }
}

public sealed class TryCoroutineResult<out T> {

    public data class Success<T>(val result: T) : TryCoroutineResult<T>()

    public data class Error<T>(val throwable: Throwable) : TryCoroutineResult<T>()
}

public inline infix fun <T> TryCoroutineResult<T>.catch(catchBlock: (Throwable) -> T): T = when (this) {
    is TryCoroutineResult.Success -> result
    is TryCoroutineResult.Error -> catchBlock(throwable)
}
