import java.util.concurrent.locks.*
import kotlin.concurrent.withLock

class Counter(initial : Int) {
	private val lock : Lock = ReentrantLock()
	private var value = initial
	
	public fun increment() : Int  {
		lock.withLock {
			return ++value
		}
	}
	
	public fun decrement() : Int  {
		lock.withLock {
			return --value
		}
	}
	
	public fun get() : Int {
		lock.withLock {
			return value
		}
	}
}

const val MAX_N = 10_000_000

val counter = Counter(0)

fun action() {
	for (n in 1..MAX_N) {
		counter.increment()
	}
}

fun main() {
	val th1 = Thread { action() }
	val th2 = Thread { action() }
	
	println(":: STARTING ::")
	
	th1.start()
	th2.start()
	
	println(":: WAITING ::")
	
	th1.join()
	th2.join()
	
	println(":: RESULTS ::")
	
	println("counter: ${ counter.get() }")
}
