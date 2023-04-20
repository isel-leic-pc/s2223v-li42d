import java.util.concurrent.atomic.AtomicReference
import java.util.Collections
import kotlin.concurrent.thread

class TreiberStack<T> {

	private class Node<T>(val value: T, val next: Node<T>?)

	private val head: AtomicReference<Node<T>?> = AtomicReference(null)
	
	fun push(value: T) {
		while (true) {
			val oldHeadNode = head.get()
			val newHeadNode = Node(value, oldHeadNode)
			if (head.compareAndSet(oldHeadNode, newHeadNode)) {
				return
			}
		}
	}

	fun pop() : T? {
		while (true) {
			val oldHeadNode = head.get()
			if (oldHeadNode == null) {
				return null
			}
			val newHeadNode = oldHeadNode.next
			if (head.compareAndSet(oldHeadNode, newHeadNode)) {
				return oldHeadNode.value
			}
		}
	}
}

fun main() {
	val NTHREADS = 100
	val NITERATIONS = 1_000_000

	val stack = TreiberStack<Int>()
	val values = Collections.synchronizedSet(mutableSetOf<Int>())

	(1..NTHREADS).map { tn ->
		var value : Int? = tn
		thread {
			(1..NITERATIONS).forEach {
				stack.push(value.nullChecked(":: [T$tn] value is null ::"))
				value = stack.pop()
			}
			values.add(value)
		}
	}.forEach { t -> t.join() }

	println(
		if (values.count() == NTHREADS) "SUCCESS" else "FAILURE"
	)
}

private inline fun <reified T> T?.nullChecked(message: String) : T {
	if (this == null) {
		println(message)
		throw NullPointerException()
	}
	return this
}


