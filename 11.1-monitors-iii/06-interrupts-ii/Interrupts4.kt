import java.util.concurrent.locks.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

fun main() {
	val lock = ReentrantLock()
	val waitSet = lock.newCondition()
	val th2 = thread(start = false, isDaemon = true) { 
		println("[T2] acquiring lock")
		lock.withLock {
			println("[T2] not really waiting")
			val tref = System.currentTimeMillis()
			while ((System.currentTimeMillis() - tref) < 10000) Thread.yield()
			println("[T2] waiting forever")
			while (true) waitSet.await()
		}
		@Suppress("UNREACHABLE_CODE")
		println("[T2] will never get here")
	}
	println("[T1] starting thread 2")
	th2.start()
	TimeUnit.SECONDS.sleep(5)
	println("[T1] interrupting thread 2")
	th2.interrupt() // only delivered at wait/sleep/join
	println("[T1] waiting for thread 2")
	th2.join(8000)
	if (th2.isAlive) {
		println("[T1] thread 2 still alive")
	} else {
		println("[T1] thread 2 terminated")
	}
}
