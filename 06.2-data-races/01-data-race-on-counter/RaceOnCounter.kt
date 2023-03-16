const val MAX_N = 10_000_000

var counter = 0

fun action() {
	for (n in 1..MAX_N) {
		++counter
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
	
	println("counter: ${ counter }")
}