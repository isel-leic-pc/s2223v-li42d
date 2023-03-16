import kotlin.random.Random
import pt.isel.leic.pc.simple.Semaphore

object TaskSource {
	private var nextTaskId = 1
	
	private fun nextTaskWithId(taskId : Int, str : String) : () -> Unit = {
		val steps  = Random.nextInt(8, 16)
		val stepMs = Random.nextLong(400,1200)
		
		println("$str :: starting ::")
		for (s in 1..steps) {
			Thread.sleep(stepMs)
			println("$str $taskId.$s")
		}
		Thread.sleep(stepMs)
		println("$str :: finished ::")	
	} 
	
	@Synchronized
	fun nextTask(str : String) = nextTaskWithId(nextTaskId++, str)
}

const val MAX_N = 8
const val LIMIT = 3
val limiter = Semaphore(LIMIT)

fun action(str : String) {
	for (n in 0..MAX_N) {
		val task = TaskSource.nextTask(str)

		limiter.acquire()
		println("$str :: pre task ::")
		task()
		println("$str :: post task ::")
		limiter.release()

		Thread.sleep(2000)
	}
}

fun main() {
	val threads = listOf(
		Thread { action("[T1]") },
		Thread { action("            [T2]") },
		Thread { action("                        [T3]") },
		Thread { action("                                    [T4]") },
		Thread { action("                                                [T5]") },
	)
	
	println(":: BEGIN ::")
	threads.forEach { it.start() }
	
	println(":: WAITING ::")
	threads.forEach { it.join() }

	println(":: DONE ::")
}

