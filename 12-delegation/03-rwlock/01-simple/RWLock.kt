class SimpleRWLock {
	val lock = ReentrantLock()
	
	private var numReaders = 0
	private var hasWriter = false

	private val waitingReaders = lock.newCondition()
	private val waitingWriters = lock.newCondition()

	fun beginRead() {
		lock.withLock {
			while (hasWriter) {
				waitingReaders.await()
			}
			numReaders++
		}
	}
	
	fun endRead() {
		lock.withLock {
			if (--numReaders == 0) {
				waitingWriters.signal()
			}
		}
	}
	
	fun beginWrite() {
		lock.withLock {
			while (numReaders > 0 || hasWriter) {
				waitingWriters.await()
			}
			hasWriter = true
		}
	}
	
	fun endWrite() {
		lock.withLock {
			hasWriter = false
			waitingWriters.signal()
			waitingReaders.signalAll()
		}
	}
}
