fun main() {

	val th1 = Thread {
		println("[T1] :: Starting ::")
		for (i in 0..7) {
			Thread.sleep(800)
			println("[T1] :: >> $i << ::")
		}
		Thread.sleep(800)
		println("[T1] :: Done ::")
	}

	val th2 = Thread {
		println("[T2] :: Starting ::")
		for (i in 0..7) {
			Thread.sleep(1200)
			println("[T2] :: >> $i << ::")
		}
		Thread.sleep(1200)
		println("[T2] :: Done ::")
	}

	println("[MN] :: Starting ::")
	th1.start()
	th2.start()

	println("[MN] :: Waiting ::")
	th1.join()
	th2.join()

	println("[MN] :: Done ::")
}
