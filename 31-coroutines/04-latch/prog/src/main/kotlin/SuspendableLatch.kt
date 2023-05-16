import kotlin.coroutines.*
import kotlinx.coroutines.*

import kotlin.concurrent.withLock

import kotlin.collections.*

import java.util.concurrent.*
import java.util.concurrent.locks.*

class SuspendableLatch() {
	private var isOpen = false
	
	private val lock = ReentrantLock()
	private val waiters = mutableListOf<Continuation<Unit>>()

	suspend fun await() {
		lock.lock()
		if (isOpen) {
			lock.unlock()
			return
		}
		
		return suspendCoroutine<Unit> { continuation ->
			waiters.add(continuation)
			lock.unlock()
		}
	}
	
	fun open() {
		lock.withLock {
			if (!isOpen) {
				isOpen = true
				for (w in waiters) {
					w.resume(Unit)
				}
			}
		}
	}
}
