package pt.isel.leic.pc.simple

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import kotlin.concurrent.withLock

class NSemaphore(private var permits : Int) {
	private val lock = ReentrantLock()
	
	private inner class UnitsRequest(
		val units: Int,
		val condition: Condition = lock.newCondition()
		var done: Boolean = false
	)

	private val unitsRequests = NodeLinkedList<UnitsRequest>()
	@Throws(InterruptedException.class)
	fun acquire(units: Int) {
		lock.withLock {
			// fast path
			if (unitsRequests.empty && units <= permits) {
				permits -= units
				return
			}
			// wait path
			val request = UnitsRequest(units)
			val requestNode = unitsRequests.enqueue(request)
			while (true) {
				try {
					request.condition.await()
				} catch (ie: InterruptedException) {
					if (request.done) {
						// Too late to be interrupted. Delay it.
						Thread.currentThread().interrupt()
						return
					} 
					// give up
					val wasFirst = unitsRequests.isHeadNode(requestNode)
					unitsRequests.remove(requestNode)
					if (wasFirst) {
						releaseWithPermits()
					}
					throw ie
				}
				if (request.done) {
					return
				}
			}
		}
	}
	
	fun release(units: Int) {
		lock.withLock {
			permits += units
			releseWithPermits()
		}
	}
	
	private fun releaseWithPermits() {
		while (true) {
			val headNode = unitsRequests.headNode
			if (headNode == null) {
				return
			}
			if (headNode.value.units <= permits) {
				permits -= headNode.value.units
				headNode.value.done = true
				headNode.value.condition.signal()
				unitsRequests.remove(headNode)
			} else {
				return
			}
		}
	}
}
