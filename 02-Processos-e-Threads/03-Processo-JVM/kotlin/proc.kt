fun main() {
	val currProc = ProcessHandle.current()
	println(":: RUNNING WITH PID ${currProc.pid()} ::")
	println("Check Task Manager > Details > java.exe")
	println("(press ENTER to stop)")
	System.console().readLine()
	
	println(":: DONE ::")
}
