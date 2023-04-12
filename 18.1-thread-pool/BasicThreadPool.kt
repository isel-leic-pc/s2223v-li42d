import java.util.*
import java.util.logging.*
import java.util.concurrent.*
import java.util.concurrent.locks.*
import kotlin.concurrent.*

class BasicThreadPool(private val nThreads: Int) {

	private val lock = ReentrantLock()
	private val workAvailable = lock.newCondition()

	private val workQueue = LinkedList<Runnable>()

	private var isShuttingDown = false
	private val terminationLatch = CountDownLatch(nThreads)

	init {
		for (n in 1..nThreads) {
			thread {
				try {
					workerThreadLoop()
				} finally {
					terminationLatch.countDown()
				}
			}
		}
	}

	@Throws(RejectedExecutionException::class)
	fun execute(workItem: Runnable) =
		lock.withLock {
			if (isShuttingDown) {
				throw RejectedExecutionException()
			}
			workQueue.add(workItem)
			workAvailable.signal()
		}

	fun shutdown() =
		lock.withLock {
			if (!isShuttingDown) {
				isShuttingDown = true
				workAvailable.signalAll()
			}
		}

	@Throws(InterruptedException::class)
	fun awaitTermination(timeout: Long, unit: TimeUnit) =
		terminationLatch.await(timeout, unit)

	sealed class GetWorkItemResult {
		object Exit : GetWorkItemResult()
		class WorkItem(val workItem: Runnable) : GetWorkItemResult()
	}

	private fun getNextWorkItem() : GetWorkItemResult {
		lock.withLock {
			do {
				if (workQueue.size > 0) {
					return GetWorkItemResult.WorkItem(workQueue.removeFirst())
				}
				if (isShuttingDown) {
					return GetWorkItemResult.Exit
				}
				try {
					workAvailable.await()
				} catch (ex: InterruptedException) {
					// Auto-interrupt of worker threads is not supported...
				}
			} while (true)
		}
	}

	private fun workerThreadLoop() {
		while (true) {
			when(val result = getNextWorkItem()) {
				is GetWorkItemResult.WorkItem -> safeRun(result.workItem)
				GetWorkItemResult.Exit        -> return
			}
		}
	}
	
	companion object {
		val logger = Logger.getLogger(BasicThreadPool::class.qualifiedName)
		
		private fun safeRun(workItem: Runnable) {
			try {
				workItem.run()
			} catch (ex: Throwable) {
				logger.log(Level.WARNING, "Exception in worker thread. Proceeding.", ex)
			}
		}
	}
}
