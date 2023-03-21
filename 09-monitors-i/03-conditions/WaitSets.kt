import java.util.concurrent.locks.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

class Counter(initial : Int, val warningLevel : Int = -1) {
	private val lock = ReentrantLock()
	private var counter = initial
	
	// Two different wait sets
	private val warningWaiters = lock.newCondition()
	private val zeroWaiters = lock.newCondition()
	
	init {
		if (initial <= 0) {
			throw IllegalArgumentException("initial value for Counter cannot be less than 1")
		}
	}

	fun awaitForZero() =
		lock.withLock() {
			while (counter > 0) {
				zeroWaiters.await()
			}
		}

	val warning : Int
		get() {
			lock.withLock {
				while (counter < warningLevel) {
					warningWaiters.await()
				}
			}
			return counter
		}

	fun increment() : Int {
		lock.withLock {
			if (++counter >= warningLevel) {
				warningWaiters.signal()
			}
			return counter
		}
	}

	fun decrement() : Int {
		lock.withLock {
			if (counter > 0) {
				if (--counter == 0) {
					zeroWaiters.signalAll()
				}
			}
			return counter
		}
	}
}	

val counter = Counter(1, warningLevel = 5)

fun aWarningWaiter() {
	println("[T${ Thread.currentThread().id }] :: waiting for warnings ::")
	val warnValue = counter.warning
	println("[T${ Thread.currentThread().id }] WARNING: ${ warnValue }")
}

fun aZeroWaiter() {
	println("[T${ Thread.currentThread().id }] :: waiting for zero ::")
	counter.awaitForZero()
	println("[T${ Thread.currentThread().id }] ZERO")
}


fun incDecAction() {
	println("[T${ Thread.currentThread().id }] :: incrementing ::")
	for (n in 1..8) {
		TimeUnit.MILLISECONDS.sleep(1500)
		val cnt = counter.increment()
		println("[T${ Thread.currentThread().id }] count = $cnt")
	}
	println("[T${ Thread.currentThread().id }] :: decrementing ::")
	for (n in 8 downTo -3) {
		TimeUnit.MILLISECONDS.sleep(500)
		val cnt = counter.decrement()
		println("[T${ Thread.currentThread().id }] count = $cnt")
	}
}


const val NUM_WARNING_THREADS = 8
const val NUM_ZERO_THREADS = 3

fun main() {
	println(":: START ::")

	for (w in 1..NUM_WARNING_THREADS) {
		// At the end of this demo, a few warning threads are still waiting for warnings.
		// Setting isDaemon=true allows for the program to terminate, despite those remaining threads.
		thread(isDaemon = true) { aWarningWaiter() }
	}

	val threads = Array<Thread>(NUM_ZERO_THREADS + 1) { idx ->
		if (idx == 0) {
			thread { 
				TimeUnit.SECONDS.sleep(1)  // WARNING: do not use sleep for synchronization!
				incDecAction()
			}
		} else {
			thread {
				aZeroWaiter()
			}
		}
	}
	
	threads.forEach { it.join() }
	
	println(":: DONE ::")
}
