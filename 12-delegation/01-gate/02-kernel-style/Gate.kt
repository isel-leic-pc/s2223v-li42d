import java.util.concurrent.atomic.*
import java.util.concurrent.locks.*
import java.util.concurrent.*
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

class Gate(private var open : Boolean = false) {
	private val lock = ReentrantLock()

	private inner class ProceedRequest(
		val condition: Condition = lock.newCondition(),
		var done: Boolean = false
	)

	private var proceedRequest = ProceedRequest()
	
	val isOpen
		get() = lock.withLock { open }
	
	fun open() {
		lock.withLock {
			if (!open) {
				open = true
				proceedRequest.done = true
				proceedRequest.condition.signalAll()
				proceedRequest = ProceedRequest()
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
			// fast path
			if (open) {
				return
			}
			// wait path
			val request = proceedRequest
			while (true) {
				proceedRequest.condition.await()
				if (request.done) {
					return
				}
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
