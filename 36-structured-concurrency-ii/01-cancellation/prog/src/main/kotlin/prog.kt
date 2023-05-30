import kotlin.coroutines.*
import kotlinx.coroutines.*

val tid : String
	inline get() = "T${ Thread.currentThread().id }(${ Thread.currentThread().name })"

suspend fun operationStep(value: Int, name: String): Int {
	println("[$tid] operation($value) : $name")
	if (value == 23) {
		throw Exception("operation step failure (value: $value)")
	}
	delay(1000)
	return value + 1
}

suspend fun operation(initialValue: Int): Int {
	var value = initialValue

	value = operationStep(value, "step 1")
	value = operationStep(value, "step 2")
	value = operationStep(value, "step 3")

	println("[$tid] operation($value) : return")
	return value
}

suspend fun main() {
	coroutineScope {
		launch {
			println("[$tid] :: OPERATION 1 ::")
			val r1 = operation(1)
			println("[$tid] r1: ${ r1 }")
		}

		launch {
			try {
				withTimeout(1600) {
					println("[$tid] :: OPERATION 2 ::")
					val r2 = operation(21)
					println("[$tid] r2: ${ r2 }")
				}
			} catch (exc: Exception) {
				println("[$tid] exc: ${ exc.message }")
				exc.printStackTrace()
				throw exc
			}
		}
	
		val job = launch {
			try {
				launch {
					println("[$tid] :: OPERATION 3 ::")
					val r3 = operation(311)
					println("[$tid] r3: ${ r3 }")
				}
				delay(500)
				launch {
					println("[$tid] :: OPERATION 4 ::")
					val r4 = operation(4111)
					println("[$tid] r4: ${ r4 }")
				}
				delay(500)
			} catch (exc: Exception) {
				println("[$tid] exc: ${ exc.message }")
				exc.printStackTrace()
				throw exc
			}
		}
		
		delay(1200)
		job.cancel()
	}

	println("## THE END ##")
}
