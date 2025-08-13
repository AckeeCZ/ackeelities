package io.github.ackeecz.ackeelities.coroutines

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * [Flow] that allows emitting values as events. Each new value replaces the previous one and
 * the value is delivered only to one collector. If there is no collector yet, the value is buffered
 * until a collector is available.
 */
public class EventFlow<T> private constructor(private val channel: Channel<T>) : Flow<T> by channel.receiveAsFlow() {

    /**
     * Creates an [EventFlow] with the specified [capacity]. The default capacity is 10, which should
     * be enough for the usual use cases of sending and consuming events. Bigger capacity than 1 is
     * ideal for being sure that the collector can process all events in cases when he is not fast enough
     * or not collecting yet. On the other hand you might want to change it to 1, if you want to actually
     * drop the old event when a new one is emitted, instead of buffering it.
     */
    public constructor(capacity: Int = 10) : this(Channel(capacity = capacity, onBufferOverflow = BufferOverflow.DROP_OLDEST))

    /**
     * Sends the [element] to the flow.
     *
     * @return `true` if the element was successfully emitted to the flow.
     */
    public fun tryEmit(element: T): Boolean = channel.trySend(element).isSuccess

    public fun asFlow(): Flow<T> = this
}
