import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.withTimeout
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

// A timeout of 0 is not supported as it changes the behaviour
// observed in suspendCancellableCoroutine, that only reacts
// to cancellation after running 'block'.

suspend inline fun <T> suspendCancellableCoroutineWithTimeout(
	timeoutMillis: Long,
	crossinline block: (CancellableContinuation<T>) -> Unit
) = withTimeout(max(1, timeoutMillis)) {
	suspendCancellableCoroutine(block = block)
}

suspend inline fun <T> suspendCancellableCoroutineWithTimeout(
	timeout: Duration,
	crossinline block: (CancellableContinuation<T>) -> Unit
) = withTimeout(timeout.coerceAtLeast(1.nanoseconds)) {
	suspendCancellableCoroutine(block = block)
}
