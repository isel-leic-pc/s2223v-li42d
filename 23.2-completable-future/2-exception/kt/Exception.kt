import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

fun delayedFailure(value: Int, delay: Long) : CompletableFuture<Int> {
	val res = CompletableFuture<Int>()

	thread {
		// after 'delay' milliseconds, complete the future with an exception
		// carrying 'value' as a message
		Thread.sleep(delay)
		res.completeExceptionally(Exception(value.toString()))
	}

	return res
}

fun main() {
	val future = delayedFailure(1234, 3000)

	println(":: promise created ::")

	// Process exceptions using 'whenComplete' and a callback with two parameters,
	// one for a value (on success) and another for an exception (on failure)
	future.whenComplete { value, exception ->
		// will run only after the future is completed (normally or exceptionally)
		if (exception == null) {
			println("++ value produced: ${ value } ++")
		} else {
			println("-- failure data: ${ exception.message } --")
		}
	}

	println(":: callback registered ::")

	println(":: waiting... ::")
}
