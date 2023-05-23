import kotlin.coroutines.*
import kotlinx.coroutines.*
import kotlin.concurrent.thread

import kotlin.coroutines.intrinsics.*

fun CoroutineScope.launchNonSuspend(
	context: CoroutineContext = EmptyCoroutineContext, 
	start: CoroutineStart = CoroutineStart.DEFAULT, 
	block: CoroutineScope.(Continuation<Unit>) -> Any
): Job = launch(context, start, block as (suspend CoroutineScope.() -> Unit))

val tid : Long
	inline get() = Thread.currentThread().id

suspend fun performSomeSuspension() {
	suspendCoroutine { cont ->
		thread {
			Thread.sleep(1500)
			cont.resume(Unit)
		}
	}
}

/*
suspend fun performSomeSuspension() {
	suspendCoroutine { cont ->
		cont.resume(Unit)
	}
}
*/

suspend fun someFunction(value: Int) : Int {
	var i = value

	println("[T$tid] Step 1: $i")
	i += 1
	performSomeSuspension()

	println("[T$tid] Step 2: $i")
	i += 1
	performSomeSuspension()

	println("[T$tid] Step 3: $i")
	i += 1
	performSomeSuspension()

	return i	
}

val someNormalFunc = ::someFunction as (Int, Continuation<Int>) -> Any

fun demo() {
	println()
	println(":: suspend function ::")
	runBlocking {
		launch { 
			val sr = someFunction(8)
			println("[T$tid] Result: $sr (suspend)")
		}
	}
	
	Thread.sleep(3000)
	
	println()
	println(":: normal? function ::")
	
	runBlocking {
		launchNonSuspend { launchCont ->
			val theFinalContinuation = 
				launchCont.context[ContinuationInterceptor]?.
					interceptContinuation(launchCont) ?: launchCont
		
			val normalFuncCont = object : Continuation<Int> {
				override val context = coroutineContext
				override fun resumeWith(result: Result<Int>) {
					if (result.isFailure) {
						println("""[T$tid] Failed: ${ result.exceptionOrNull()?.message ?: "-- unknown --" } (continuation)""")
					} else {
						println("""[T$tid] Result: ${ result.getOrNull() } (continuation)""")
					}
					theFinalContinuation.resume(Unit)
				}
			}
			
			val rnf = someNormalFunc(17, normalFuncCont)
			println("[T$tid] Result: $rnf (normal?)")
			if (rnf == COROUTINE_SUSPENDED) {
				COROUTINE_SUSPENDED
			} else {
				theFinalContinuation.resume(Unit)
				Unit
			}
		}
	}
}

fun main() {
	println("## STARTING DEMO ##")
	demo()
	println("## END OF DEMO ##")
	Thread.sleep(10000)
}
