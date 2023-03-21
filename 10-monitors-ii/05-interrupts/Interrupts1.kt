import java.util.concurrent.locks.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

fun main() {
	val lock = ReentrantLock()

	// First step: get hold of the lock
	println("[T1] acquiring the lock")
	lock.lock()

	// Now run a second thread
	println("[T1] starting thread 2")
	val th2 = thread(isDaemon = true) { 
		println("[T2] trying to acquire lock")
		// Thread 2 will not be able to acquire the lock, as T1 is holding it.
		lock.lock()
		println("[T2] will never get here")
	}

	TimeUnit.SECONDS.sleep(5)

	// Interrupt thread 2
	println("[T1] interrupting thread 2")
	th2.interrupt()
	
	// Thread 2 will not terminate, despite the interrupt
	println("[T1] waiting for thread 2")
	th2.join(5000)

	if (th2.isAlive) {
		println("[T1] thread 2 still alive")
	} else {
		println("[T1] thread 2 terminated")
	}
}
