import java.util.concurrent.*
import kotlin.random.*

val customerCounters = ConcurrentHashMap<Int,Int>()

const val MAX_N = 100_000
const val MAX_ID = 1_000

fun action() {
	for (n in 1..MAX_N) {
		val id = Random.nextInt(0,MAX_ID);
		customerCounters.put(id, (customerCounters.get(id) ?: 0) + 1);
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
	println("Total: ${ customerCounters.values.fold(0, { a, b -> a+b }) }")
}
