import java.util.concurrent.*

val executor = Executors.newWorkStealingPool()

fun main() {

	val mainTid = Thread.currentThread().id
	println("[T$mainTid] :: STARTING ::")

	val res = (0..15).map {
		executor.submit<Int> {
			if (it % 5 == 4) {
				throw Exception("Emotional damage!")
			}
			it * 2
		}
	}
	
	println("[T$mainTid] :: waiting for results ::")
	executor.shutdown()
	if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
		println("[T$mainTid] :: UNEXPECTED FAILURE ::")
	}

	println("[T$mainTid] :: RESULTS ::")
	res.forEachIndexed { idx, rf ->
		try {
			println("res[$idx]: ${ rf.get() }")
		} catch (ex: ExecutionException) {
			println("res[$idx]: ${ ex.cause?.message ?: "???" }")
		}
	}

	println("[T$mainTid] :: DONE ::")
}
