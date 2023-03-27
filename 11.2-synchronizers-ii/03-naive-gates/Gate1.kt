import java.util.concurrent.atomic.*
import java.util.concurrent.locks.*
import java.util.concurrent.*
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

// ###############################################
// #                                             #
// # WARNING: Naive code. Do not use as example! #
// #                                             #
// ###############################################


class Gate(private var open : Boolean = false) {
	private val lock = ReentrantLock()
	private val waitSet = lock.newCondition()
	
	val isOpen
		get() = lock.withLock { open }
	
	fun open() {
		lock.withLock {
			if (!open) {
				open = true;
				waitSet.signalAll()
			}
		}
	}
	
	fun close() {
		lock.withLock {
			if (open) {
				open = false;
			}
		}
	}
	
	fun await() {
		lock.withLock {
			while (!open) {
				waitSet.await()
			}
		}
	}
}


val gate = Gate()
val done = AtomicInteger(0)

fun action() {
	gate.await()
	done.incrementAndGet()
}

fun main() {
	repeat(100) { thread(isDaemon = true) { action() } }
	
	TimeUnit.SECONDS.sleep(1)     // WARNING: do not use sleep for synchronization!
	
	gate.open()
	TimeUnit.MICROSECONDS.sleep(1)
	gate.close()

	TimeUnit.SECONDS.sleep(5)     // WARNING: do not use sleep for synchronization!

	println("terminated threads: ${ done.get() }")
}
