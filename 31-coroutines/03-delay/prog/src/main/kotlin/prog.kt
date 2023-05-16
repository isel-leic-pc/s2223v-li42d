import kotlin.coroutines.*
import kotlinx.coroutines.*

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val tid : Long
	inline get() = Thread.currentThread().id

private val executor = Executors.newSingleThreadScheduledExecutor()

fun terminate() { executor.shutdown() }

suspend fun xdelay(time: Long) {
	suspendCoroutine<Unit> { continuation : Continuation<Unit> ->
		executor.schedule({ continuation.resume(Unit) }, time, TimeUnit.MILLISECONDS)
	}
	//println("... waking up ...")
}

fun main() {
	runBlocking {
		launch { 
			xdelay(1500)
			println("++ [T$tid] Message 3 ++")
			xdelay(1000)
			println("++ [T$tid] Message 5 ++")
		}

		launch {
			xdelay(1000)
			println("++ [T$tid] Message 2 ++")
			xdelay(1000)
			println("++ [T$tid] Message 4 ++")
		}

		launch {
			xdelay(500)
			println("++ [T$tid] Message 1 ++")
			xdelay(2500)
			println("++ [T$tid] Message 6 ++")
		}

		println(":: [T$tid] STARTING ::")
	}
	terminate()
}
