import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

fun delayedValue(value: Int, delay: Long) : CompletableFuture<Int> {
	val res = CompletableFuture<Int>()
	
	thread {
		// after 'delay' milliseconds, complete the future with 'value'
		Thread.sleep(delay)
		res.complete(value)
	}
	
	return res
}

fun main() {
	val future = delayedValue(1234, 3000)

	println(":: future created ::")

	future.thenAccept { value ->
		// will run only after the future is completed normally
		println("++ value produced: ${ value } ++")
	}

	println(":: callback registered ::")

	println(":: waiting... ::")
}
