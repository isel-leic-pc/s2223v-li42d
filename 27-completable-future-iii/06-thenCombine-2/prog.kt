import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

import java.lang.Thread.*

fun <T,U,V> CompletableFuture<T>.depoisCombina(
	other: CompletionStage<U>,
	fn: Function2<T, U, V>
) : CompletableFuture<V> {
	val fut = CompletableFuture<V>()

	this.whenComplete  {
		res1, err1 ->
		other.whenComplete {
			res2, err2 ->
				if (err1 != null) {
					fut.completeExceptionally(err1)
				} else if (err2 != null) {
					fut.completeExceptionally(err2)
				} else {
					fut.complete(fn(res1, res2))
				}
		}
	}

	return fut
}

fun asyncFunction1() =
	CompletableFuture.supplyAsync<String> {
		println("++ AUXILIAR [T${ currentThread().id }] ++")
		sleep(3000)
		println("++ FINISHED [T${ currentThread().id }] ++")
		"ISEL"
		//throw Exception("async1 failed")
	}

fun asyncFunction2(base: Int) =
	CompletableFuture.supplyAsync<Int> {
		println("++ AUXILIAR [T${ currentThread().id }] ++")
		sleep(2000)
		println("++ FINISHED [T${ currentThread().id }] ++")
		base + 23
		//throw Exception("async2 failed")
	}

fun main() {
	println(":: STARTING [T${ currentThread().id }] ::")
	sleep(2000)

	val asyncRes1 = asyncFunction1()

	val asyncRes2 = asyncFunction2(2000)

	val asyncRes12 = asyncRes1.depoisCombina(asyncRes2) { txt, num -> "$txt - $num"}

	val asyncRes = asyncRes12.handle { res, err ->
		println("~~ RESULTS [T${ currentThread().id }] ~~")
		sleep(2000)
		if (err == null) {
			println("result: $res")
		} else {
			println("error: $err")
		}
	}

	println(":: ALL READY [T${ currentThread().id }] ::")
	
	asyncRes.join()

	println(":: ALL DONE [T${ currentThread().id }] ::")
}
