import java.util.concurrent.*

val executor = Executors.newWorkStealingPool()

fun main() {

	val mainTid = Thread.currentThread().id
	println("[T$mainTid] :: STARTING ::")

	val nProc = Runtime.getRuntime().availableProcessors()
	val latch = CountDownLatch(1)
	for (w in 1..nProc) {
		executor.execute {
			val wtid = Thread.currentThread().id
			println("[T$wtid] is worker #$w")
			latch.await()
			for (n in 0..3) {
				executor.execute {
					val tid = Thread.currentThread().id
					println("[T$tid] with task #$w$n")
				}
			}
		}
	}
	Thread.sleep(100)
	latch.countDown()
	
	println("[T$mainTid] :: WAITING ::")
	executor.shutdown()
	if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
		println("[T$mainTid] :: timed out ::")
	} else {
		println("[T$mainTid] :: DONE ::")
	}
}
	