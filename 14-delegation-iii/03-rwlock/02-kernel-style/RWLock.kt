import pt.isel.pc.utils.NodeLinkedList
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class RWLock {
	val lock = ReentrantLock()
	
	private var numReaders = 0
	private var writerThread: Thread? = null

	private inner class ReadRequest(
		var numWaiting: Int = 0,
		val condition: Condition = lock.newCondition(),
		var done: Boolean = false
	)

	private inner class WriteRequest(
		val thread: Thread = Thread.currentThread(),
		val condition: Condition = lock.newCondition(),
		var done: Boolean = false
	)

	private var readRequest = ReadRequest()
	private val waitingWriters = NodeLinkedList<WriteRequest>()

	@Throws(InterruptedException::class)
	fun beginRead(timeout: Duration) : Boolean {
		lock.withLock {
			// fast path
			if (writerThread == null && waitingWriters.empty) {
				numReaders++
				return true
			}
			// wait path
			val request = readRequest
			request.numWaiting++
			var remainingTime = timeout.inWholeNanoseconds
			while (true) {
				try {
					remainingTime = request.condition.awaitNanos(remainingTime)
				} catch (ie: InterruptedException) {
					if (request.done) {
						return true
					}
					quitWaitingForReading(request)
					throw ie
				}
				if (request.done) {
					return true
				}
				if (remainingTime <= 0) {
					quitWaitingForReading(request)
					return false
				}
			}
		}
	}
	
	fun endRead() {
		lock.withLock {
			if (numReaders == 0) {
				throw IllegalStateException("endRead called while no ongoing reads")
			}
			if (--numReaders == 0) {
				if (waitingWriters.notEmpty) {
					unblockFirstWaitingWriter()
				}
			}
		}
	}
	
	@Throws(InterruptedException::class)
	fun beginWrite(timeout: Duration) : Boolean {
		lock.withLock {
			// fast path
			if (numReaders == 0 && writerThread == null) {
				writerThread = Thread.currentThread()
				return true
			}
			// wait path
			val request = WriteRequest()
			val requestNode = waitingWriters.enqueue(request)
			var remainingTime = timeout.inWholeNanoseconds
			while (true) {
				try {
					remainingTime = request.condition.awaitNanos(remainingTime)
				} catch (ie: InterruptedException) {
					if (request.done) {
						return true
					}
					quitWaitingForWriting(requestNode)
					throw ie
				}
				if (request.done) {
					return true
				}
				if (remainingTime <= 0) {
					quitWaitingForWriting(requestNode)
					return false
				}
			}
		}
	}
	
	fun endWrite() {
		lock.withLock {
			if (Thread.currentThread() != writerThread) {
				throw IllegalStateException("Thread calling endWrite is not the current writer")
			}
			if (waitingWriters.notEmpty) {
				unblockFirstWaitingWriter()
			} else {
				unblockAllWaitingReaders()
			}
		}
	}
	
	private fun unblockFirstWaitingWriter() {
		val firstWaitingWriter = waitingWriters.pull().value
		writerThread = firstWaitingWriter.thread   // effect delegation
		firstWaitingWriter.done = true
		firstWaitingWriter.condition.signal()
	}
	
	private fun unblockAllWaitingReaders() {
		val request = readRequest
		readRequest = ReadRequest()
		numReaders += request.numWaiting    // effect delegation
		request.done = true
		request.condition.signalAll()
	}
	
	private fun quitWaitingForReading(request: ReadRequest) {
		request.numWaiting--
	}

	private fun quitWaitingForWriting(requestNode: NodeLinkedList.Node<WriteRequest>) {
		waitingWriters.remove(requestNode)
		if (waitingWriters.empty) {
			unblockAllWaitingReaders()
		}
	}
}
