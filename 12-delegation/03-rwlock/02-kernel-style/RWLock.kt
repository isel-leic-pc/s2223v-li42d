// INCOMPLETO

class SimpleRWLock {
	val lock = ReentrantLock()
	
	private var numReaders = 0
	private var writerThread: Thread? = null

	private inner class ReadRequest(
		val condition: Condition = lock.newCondition()
		var done: Boolean = false
	)

	private inner class WriteRequest(
		val thread: Thread = Thread.currentThread() 
		val condition: Condition = lock.newCondition()
		var done: Boolean = false
	)

	private val readRequest = ReadRequest()
	private val waitingWriters = NodeLinkedList<WriteRequest>()

	fun beginRead() {
		lock.withLock {
			// fast path
			if (writerThread == null && waitingWriter.empty) {
				numReaders++
				return
			}
			// wait path
			val request = readRequest
			while (true) {
				request.condition.await()
				
				if (request.done) {
					return
				}
			}
		}
	}
	
	fun endRead() {
		lock.withLock {

		}
	}
	
	fun beginWrite() {
		lock.withLock {
			// fast path
			if (numReaders == 0 && writerThread == null) {
				writerThread = Thread.currentThread()
				return
			}
			// wait path
			val request = WriteRequest()
			val requestNode = waitingWriters.enqueue(request)
			while (true) {
				request.condition.await()
				
				if (request.done) {
					return
				}
			}
		}
	}
	
	fun endWrite() {
		lock.withLock {

		}
	}
}
