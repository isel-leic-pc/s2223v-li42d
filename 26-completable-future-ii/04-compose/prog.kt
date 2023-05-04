import java.util.concurrent.CompletableFuture

import java.lang.Thread.*

fun asyncFunction1(base: Int) : CompletableFuture<Int> =
	CompletableFuture.supplyAsync<Int> {
		println("++ AUXILIAR [T${ currentThread().id }] ++")
		sleep(2000)
		println("++ FINISHED [T${ currentThread().id }] ++")
		base + 23
	}

fun asyncFunction2(num: Int) : CompletableFuture<String> =
	CompletableFuture.supplyAsync<String> {
		println("++ AUXILIAR [T${ currentThread().id }] ++")
		sleep(3000)
		println("++ FINISHED [T${ currentThread().id }] ++")
		"ISEL - $num"
	}

fun printResults(res: String) {
	println("~~ RESULTS [T${ currentThread().id }] ~~")
	sleep(2000)
	println("result: ${ res }")
}

fun main() {
	println(":: STARTING [T${ currentThread().id }] ::")
	sleep(2000)

	val asyncRes1 = asyncFunction1(2000)
	
	val asyncRes12 = asyncRes1.thenCompose(::asyncFunction2)

	val asyncRes = asyncRes12.thenAccept(::printResults)

	println(":: ALL READY [T${ currentThread().id }] ::")

	asyncRes.join()
	
	println(":: ALL DONE [T${ currentThread().id }] ::")
}
