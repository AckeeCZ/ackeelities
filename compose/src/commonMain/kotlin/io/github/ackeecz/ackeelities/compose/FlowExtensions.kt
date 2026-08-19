package io.github.ackeecz.ackeelities.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * Collects this [Flow] in a lifecycle-aware manner, invoking [onEach] for every emitted value.
 *
 * Values are collected only while the lifecycle of the current [LocalLifecycleOwner] is at least
 * in the [minActiveState]. When the lifecycle falls below [minActiveState], the collection is
 * cancelled and it is restarted once the lifecycle reaches [minActiveState] again.
 *
 * Values are collected using [collectLatest], so the processing of the previous value is cancelled
 * when a new value is emitted. The latest [onEach] lambda passed to this function is always used
 * without restarting the collection.
 */
@Composable
public fun <T> Flow<T>.CollectLifecycleAware(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEach: suspend (value: T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareFlow = remember(this, lifecycleOwner, minActiveState) {
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
    }
    val currentOnEach by rememberUpdatedState(onEach)
    LaunchedEffect(lifecycleAwareFlow) {
        lifecycleAwareFlow.collectLatest { currentOnEach(it) }
    }
}
