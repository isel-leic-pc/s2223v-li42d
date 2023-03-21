import java.util.concurrent.locks.*

//
// A version of kotlin.concurrent.withLock extension method
//
public inline fun <T> Lock.usingLock(action : () -> T) : T {
	lock()
	try {
		return action()
	} finally {
		unlock()
	}
}
	
val locker = ReentrantLock()

// :::::::: DEMO 1 ::::::::
// While T1 holds the lock, T2 cannot obtain it
//
fun demo1Thread1() {
	println(":: DEMO 1 START ::")
	val th2 = Thread { demo1Thread2() }
	println("[T1] acquiring the lock...")
	locker.usingLock {
		println("[T1] lock acquired...")
		th2.start()
		Thread.sleep(5000)
		println("[T1] releasing the lock...")
	}
	println("[T1] lock released")
	th2.join()
	println("== DEMO 1 END ==")
}

fun demo1Thread2() {
	println("     [T2] waiting for lock...")
	locker.usingLock {
		println("     [T2] lock acquired...")
		Thread.sleep(2000)
		println("     [T2] releasing the lock...")
	}
	println("     [T2] lock released")
}

// :::::::: DEMO 2 ::::::::
// While T1 holds the lock, T2 cannot obtain it.
// T1 throws an exception while holding the lock.
//
fun demo2Thread1() {
	println(":: DEMO 2 START ::")
	val th2 = Thread { demo2Thread2() }
	try {
		println("[T1] acquiring the lock...")
		locker.usingLock {
			println("[T1] lock acquired...")
			th2.start()
			Thread.sleep(5000)
			println("[T1] releasing the lock...")
			throw Exception("Running away")
			@Suppress("UNREACHABLE_CODE")
			println("[T1] shouldn't be running!")
		}
		println("[T1] shouldn't be here!")
		th2.join()
		println("== DEMO 2 FAILED ==")
	} catch (e : Exception) {
		println("[T1] lock released")
		th2.join()
		println("== DEMO 2 END ==")
	}
}

fun demo2Thread2() {
	println("     [T2] waiting for lock...")
	locker.usingLock {
		println("     [T2] lock acquired...")
		Thread.sleep(2000)
		println("     [T2] releasing the lock...")
	}
	println("     [T2] lock released")
}

// :::::::: MAIN ::::::::

fun main() {
	val demo1 = Thread { demo1Thread1() }
	demo1.start()
	demo1.join()

	println()

	val demo2 = Thread { demo2Thread1() }
	demo2.start()
	demo2.join()
}
