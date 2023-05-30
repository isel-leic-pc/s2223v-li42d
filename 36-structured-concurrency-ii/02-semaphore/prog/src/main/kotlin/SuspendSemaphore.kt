import java.util.concurrent.locks.ReentrantLock
import java.util.LinkedList

import kotlin.coroutines.*
import kotlinx.coroutines.*

import kotlin.concurrent.withLock
import kotlin.time.Duration

class SuspendSemaphore(initialPermits: Int) {
	private val mutex = ReentrantLock()
	private var currentPermits = initialPermits

	private class Request(val permits: Int) {
		var continuation: Continuation<Unit>? = null
		var done = false
	}
	private val waiters = LinkedList<Request>()

	suspend fun acquire(permits: Int, timeout: Duration) {
		// fast-path
		mutex.lock()
		if (currentPermits >= permits) {
			currentPermits -= permits
			mutex.unlock()
			return
		}
		
		// suspend-path
		val request = Request(permits)

		try {
			suspendCancellableCoroutineWithTimeout(timeout) { cont ->
				request.continuation = cont
				waiters.add(request)
				mutex.unlock()
			}
		} catch (cancelExc: CancellationException) {
			handleCancellation(cancelExc, request)
		}
	}

	fun release(permits: Int) {
		val releaseList = LinkedList<Continuation<Unit>?>()
		mutex.withLock {
			currentPermits += permits
			while (waiters.size > 0 &&
			       currentPermits >= waiters.first.permits) {
				val request = waiters.removeFirst()
				request.done = true
				currentPermits -= request.permits
				releaseList.add(request.continuation)
			}
		}
		releaseList.forEach { continuation ->
			continuation?.resume(Unit)
		}
	}

	private fun handleCancellation(cause: Throwable?, request: Request) {
		mutex.withLock {
			if (!request.done) {
				waiters.remove(request)
				throw cause ?: Exception("-- unexpected call to handleCancellation --")
			}
			// else succeed 
			// NOTE: the coroutine is still cancelled; the caller
			//       is responsible for dealing with that situation
			//       (acquire returned with permits but the 
			//        coroutine was cancelled simultaneously)
		}
	}
}
