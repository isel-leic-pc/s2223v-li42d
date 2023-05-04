import java.util.concurrent.CompletableFuture

import java.lang.Thread.*

fun asyncFunction1() : CompletableFuture<String> =
	CompletableFuture.supplyAsync<String> {
		println("++ AUXILIAR [T${ currentThread().id }] ++")
		sleep(3000)
		println("++ FINISHED [T${ currentThread().id }] ++")
		"ISEL"
	}

fun asyncFunction2(base: Int) : CompletableFuture<Int> =
	CompletableFuture.supplyAsync<Int> {
		println("++ AUXILIAR [T${ currentThread().id }] ++")
		sleep(4000)
		println("++ FINISHED [T${ currentThread().id }] ++")
		base + 23
	}

fun main() {
	println(":: STARTING [T${ currentThread().id }] ::")
	sleep(2000)

	val asyncRes1 = asyncFunction1()
	
	val asyncRes2 = asyncFunction2(2000)
	
	val asyncRes12 = asyncRes1.thenCombine(asyncRes2) {
		txt, num -> "$txt - $num"
	}

	val asyncRes = asyncRes12.thenAccept { res ->
		println("~~ RESULTS [T${ currentThread().id }] ~~")
		sleep(2000)
		println("result: ${ res }")
	}

	/*
		asyncFunction1()
			.thenCombine(asyncFunction2(2000), { txt, num -> "$txt - $num" })
			.thenAccept { res -> ... }
	*/

	println(":: ALL READY [T${ currentThread().id }] ::")

	asyncRes.join()
	
	println(":: ALL DONE [T${ currentThread().id }] ::")
}
