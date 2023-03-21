import java.util.concurrent.locks.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

class Event {
	private val lock = ReentrantLock()
	private var set = false
	
	private val waiters = lock.newCondition()
	
	fun set() {
		lock.withLock {
			if (!set) {
				println("[T${ Thread.currentThread().id }] setting the event")
				set = true
				waiters.signal() // Although set=true at this moment, the signalled thread may see it as false.
			}
		}
	}
	
	fun await() {
		lock.withLock {
			// Always check blocking conditions with a 'while' loop. The blocking condition must
			// be rechecked after leaving waiters.await().
			// In this example, both barging and spurious wake-ups can happen.
			if (!set) { 
				println("[T${ Thread.currentThread().id }] waiting for the event")
				
				// A waking thread must reacquire the lock. Another thread might invoke this same method
				// and execute set=false before the waking thread is able to run again.
				waiters.await()
				
				println("[T${ Thread.currentThread().id }] woke up from await")
			}
			println("[T${ Thread.currentThread().id }] seeing set=${ set }; resetting the event back to false")
			set = false
		}
	}
}

val event = Event()

fun waiter() {
	println("[T${ Thread.currentThread().id }] :: new waiter ::")
	event.await()
	println("[T${ Thread.currentThread().id }] :: done ::")
}

//
// The message from line 36 will likely appear twice, despite event.set() being called only once.
// However, the output of this demo is not fully predictable, so you might not observe that effect.
//
fun main() {
	repeat(100) { thread(isDaemon = true) { waiter() } }
	event.set()
	TimeUnit.SECONDS.sleep(5)
}
