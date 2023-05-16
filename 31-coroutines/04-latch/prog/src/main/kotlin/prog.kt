import kotlin.coroutines.*
import kotlinx.coroutines.*

val tid : Long
	inline get() = Thread.currentThread().id

fun main() {
	runBlocking {
		val latch = SuspendableLatch()

		launch { 
			println("++ Waiting 1 ++")
			latch.await()
			println("++ DONE #1 ++")
		}

		launch {
			println("++ Waiting 2 ++")
			latch.await()
			println("++ DONE #2 ++")
		}

		launch {
			println("++ Waiting 3 ++")
			latch.await()
			println("++ DONE #3 ++")
		}

		println(":: STARTING ::")
		delay(10000)
		println(":: RELEASING ::")
		
		latch.open()
	}
}
