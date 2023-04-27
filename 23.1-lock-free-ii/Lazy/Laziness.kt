import java.util.concurrent.TimeUnit

class Laziness {
	
	private val eagerValue = calculateHardValue("eager")
	
	private val lazyValue by lazy { calculateHardValue("lazy") }
	
	fun showEagerValue() {
		println("eager: $eagerValue")
	}
	
	fun showLazyValue() {
		println("lazy: $lazyValue")
	}
	
	private fun calculateHardValue(label: String) : Int {
		println(":: calculating for $label ::")
		TimeUnit.SECONDS.sleep(8)
		return 8
	}
}

fun main() {
	println(":: STARTING ::")
	
	val obj = Laziness()

	println(":: RUNNING ::")
	TimeUnit.SECONDS.sleep(2)
	
	println(":: EAGER ::")
	obj.showEagerValue()
	
	TimeUnit.SECONDS.sleep(2)

	println(":: LAZY ::")
	obj.showLazyValue()
}
