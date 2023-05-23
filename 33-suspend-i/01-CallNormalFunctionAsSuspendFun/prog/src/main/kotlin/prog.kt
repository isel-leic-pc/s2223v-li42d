import kotlin.coroutines.*
import kotlinx.coroutines.*
import kotlin.concurrent.thread

import kotlin.coroutines.intrinsics.*

val tid : Long
	inline get() = Thread.currentThread().id

suspend fun suspendForSomeTime1(msToWaste: Long) : Long {
	delay(msToWaste)
	return msToWaste * 2
}

suspend fun function1(initialMs: Long) : Long {
	var ms = initialMs
	println("[T$tid] Step 1: $ms")
	ms = suspendForSomeTime1(ms)
	println("[T$tid] Step 2: $ms")
	ms = suspendForSomeTime1(ms)
	println("[T$tid] Step 3: $ms")
	ms = suspendForSomeTime1(ms)
	return ms
}

fun wasteSomeTime(msToWaste: Long, cont: Continuation<Long>) : Any {
	thread {  // don't launch new threads at home/work; use thread pools
		Thread.sleep(msToWaste)
		
		val theContinuation = 
			cont.context[ContinuationInterceptor]?.interceptContinuation(cont) ?: cont
		
		theContinuation.resume(msToWaste * 2)
	}
	return COROUTINE_SUSPENDED
}

val suspendForSomeTime2 = ::wasteSomeTime as (suspend (Long) -> Long)

suspend fun function2(initialMs: Long) : Long {
	var ms = initialMs
	println("[T$tid] Step 1: $ms")
	ms = suspendForSomeTime2(ms)
	println("[T$tid] Step 2: $ms")
	ms = suspendForSomeTime2(ms)
	println("[T$tid] Step 3: $ms")
	ms = suspendForSomeTime2(ms)
	return ms
}

fun main() {
	runBlocking {
		println("## A REAL SUSPEND FUNCTION ##")
		launch(Dispatchers.IO) {
			val res = function1(1000)
			println("[T$tid] Result: $res (suspend)")
		}
	}
	
	println()
	Thread.sleep(3000)
	
	runBlocking {
		println("## A 'FAKE' SUSPEND FUNCTION ##")
		launch(Dispatchers.IO) {
			val res = function2(1000)
			println("[T$tid] Result: $res (normal?)")
		}
	}
}
