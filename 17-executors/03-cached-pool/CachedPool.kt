import java.util.concurrent.*

val executor = Executors.newCachedThreadPool()

fun main() {

	val mainTid = Thread.currentThread().id
	println("[T$mainTid] :: STARTING ::")

	for (n in 0..15) {
		Thread.sleep(1)
		executor.execute {
			val tid = Thread.currentThread().id
			println("[T$tid] executing task #$n")		
			Thread.sleep(10)
		}
	}

	Thread.sleep(65000)
	println("----------------")

	for (n in 16..31) {
		Thread.sleep(1)
		executor.execute {
			val tid = Thread.currentThread().id
			println("[T$tid] executing task #$n")
			Thread.sleep(5)
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