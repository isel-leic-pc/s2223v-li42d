import java.util.concurrent.locks.*
import kotlin.concurrent.withLock

class Counter(initial : Int) {
	private val lock = ReentrantLock()
	private var counter = initial
	
	val value : Int
		get() {
			var res : Int
			println("[T${ Thread.currentThread().id }] waiting for lock")
			lock.withLock {
				println("[T${ Thread.currentThread().id }] inside the locked area")
				res = counter
				println("[T${ Thread.currentThread().id }] exiting the locked area")
			}
			println("[T${ Thread.currentThread().id }] lock released")
			return res
		}

	fun increment() {
		// Hold the lock...
		lock.withLock() {
			println("[T${ Thread.currentThread().id }] executing increment with lock")
			Thread.sleep(5000)
			
			// Reading 'value' invokes its getter, which requires reacquiring the same lock
			counter = value + 1
			println("[T${ Thread.currentThread().id }] still executing increment with lock")
			
			Thread.sleep(5000)
			println("[T${ Thread.currentThread().id }] just about to release the increment lock")
		}
	}
}


val counter = Counter(0)

// T1 invokes increment, which holds the lock for a few seconds
// T2 will have to wait in order to execute counter.value.get()
//
fun action1() {
	println(":: DEMO START ::")
	val th2 = Thread { action2() }
	th2.start()
	
	counter.increment()
	
	th2.join()
	println(":: DEMO END ::")
}

fun action2() {
	Thread.sleep(2000)  // WARNING: do not rely on sleep for synchronization!
	
	println("${ counter.value }")
}

fun main() {
	with(Thread { action1() }) {
		start()
		join()
	}
}
