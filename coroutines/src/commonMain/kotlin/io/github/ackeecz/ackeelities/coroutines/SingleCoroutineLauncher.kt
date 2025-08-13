package io.github.ackeecz.ackeelities.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

/**
 * A utility class that allows launching a single coroutine at a time.
 * If a new coroutine is launched while a previous one is still running, the previous one will be cancelled.
 */
public class SingleCoroutineLauncher(private val coroutineScope: CoroutineScope) {

    private var job: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    public fun launch(action: suspend CoroutineScope.() -> Unit) {
        job = coroutineScope.launch {
            action(this)
        }
    }
}

public fun <T> Flow<T>.launchIn(singleCoroutineLauncher: SingleCoroutineLauncher) {
    singleCoroutineLauncher.launch {
        launchIn(this)
    }
}
