package pt.isel.leic.pc.simple

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import kotlin.concurrent.withLock

class Semaphore(private var permits : Int) {
	private val lock = ReentrantLock()
	private val waitSet = lock.newCondition()

	init {
		println("###########################")
		println("#                         #")
		println("# YES, IT'S OUR SEMAPHORE #")
		println("#                         #")
		println("###########################")
	}

	fun acquire() {
		lock.withLock {
			while (permits <= 0) {
				waitSet.await()
			}
			--permits
		}
	}
	
	fun release() {
		lock.withLock {
			++permits
			waitSet.signal()
		}
	}
}