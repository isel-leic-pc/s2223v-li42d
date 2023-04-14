import java.util.concurrent.*

val executor = Executors.newSingleThreadExecutor()

fun main() {

	val mainTid = Thread.currentThread().id
	println("[T$mainTid] :: STARTING ::")
	
	val res = (0..7).map { 
		executor.submit<Int> {
			val tid = Thread.currentThread().id
			println("[T$tid] :: Task #$it ::")
			try {
				for (s in 0..7) {
					Thread.sleep(500)
				}
				it
			} catch (ex: InterruptedException) {
				println("[T$tid] :: Task #$it INTERRUPTED ::")
				throw ex
			}
		}
	}
	
	Thread.sleep(10000)
	println("[T$mainTid] :: CANCELLING ::")
	res.forEachIndexed { idx, fres ->
		println("[T$mainTid] cancelling $idx : ${ fres.cancel(true) }")
	}

	println("[T$mainTid] :: RESULTS ::")
	res.forEachIndexed { idx, fres ->
		try {
			println("[T$mainTid] res[$idx] : ${ fres.get() }")
		} catch (ex: Throwable) {
			println("[T$mainTid] res[$idx] : EXCEPTION\n$ex")
		}
	}
	
	println("[T$mainTid] :: DONE ::")
	executor.shutdown()
}
