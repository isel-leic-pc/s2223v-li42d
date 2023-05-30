import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

suspend inline fun <T> suspendCancellableCoroutineWithTimeout(
	timeoutMillis: Long,
	crossinline block: (CancellableContinuation<T>) -> Unit
) = withTimeout(timeoutMillis) {
	suspendCancellableCoroutine(block = block)
}

suspend inline fun <T> suspendCancellableCoroutineWithTimeout(
	timeout: Duration,
	crossinline block: (CancellableContinuation<T>) -> Unit
) = withTimeout(timeout) {
	suspendCancellableCoroutine(block = block)
}
