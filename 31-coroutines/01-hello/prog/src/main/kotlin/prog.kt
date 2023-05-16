import kotlinx.coroutines.*

fun main() = runBlocking {
	launch { 
		delay(8000)
		println(", world!")
	}

	print("Hello")
}
