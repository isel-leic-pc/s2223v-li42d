import java.util.concurrent.*

val executor = Executors.newFixedThreadPool(
	Runtime.getRuntime().availableProcessors()
)

fun main() {

	val mainTid = Thread.currentThread().id
	println("[T$mainTid] :: STARTING ::")

	for (n in 0..15) {
		executor.execute {
			if (n < 3) Thread.sleep(100)
			val tid = Thread.currentThread().id
			println("[T$tid] executing task #$n")		
		}
	}

	println("[T$mainTid] :: WAITING ::")
	executor.shutdown()

	if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
		println("[T$mainTid] :: timed out ::")
	} else {
		println("[T$mainTid] :: DONE ::")
	}
}