import kotlinx.coroutines.*

val tid : Long
	inline get() = Thread.currentThread().id

fun main() = runBlocking {
	launch { 
		delay(1500)
		println("++ [T$tid] Message 3 ++")
		delay(1000)
		println("++ [T$tid] Message 5 ++")
	}

	launch {
		delay(1000)
		println("++ [T$tid] Message 2 ++")
		delay(1000)
		println("++ [T$tid] Message 4 ++")
	}

	launch {
		delay(500)
		println("++ [T$tid] Message 1 ++")
		delay(2500)
		println("++ [T$tid] Message 6 ++")
	}

	println(":: [T$tid] STARTING ::")
}
