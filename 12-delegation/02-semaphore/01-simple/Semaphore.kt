package pt.isel.leic.pc.simple

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import kotlin.concurrent.withLock

class NSemaphore(private var permits : Int) {
	private val lock = ReentrantLock()
	private val waitSet = lock.newCondition()

	fun acquire(units: Int) {
		lock.withLock {
			while (permits < units) {
				waitSet.await()
			}
			permits -= units
		}
	}
	
	fun release(units: Int) {
		lock.withLock {
			permits += units
			waitSet.signalAll()
		}
	}
}
