package io.github.ackeecz.ackeelities.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine scope that is bound to the application lifecycle
 */
public interface AppCoroutineScope : CoroutineScope {

    public companion object {

        /**
         * Creates a new [AppCoroutineScope] with the given [coroutineContext].
         * The default context is a [SupervisorJob] with [Dispatchers.Default].
         */
        public operator fun invoke(
            coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default,
        ): AppCoroutineScope {
            return AppCoroutineScopeImpl(coroutineContext)
        }
    }
}

private class AppCoroutineScopeImpl(override val coroutineContext: CoroutineContext) : AppCoroutineScope
