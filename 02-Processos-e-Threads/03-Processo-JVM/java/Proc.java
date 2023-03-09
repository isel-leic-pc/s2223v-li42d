public class Proc {
	public static void main(String[] args) {
		final ProcessHandle currProc = ProcessHandle.current();
		System.out.println(":: RUNNING WITH PID " + currProc.pid() + " ::");
		System.out.println("Check Task Manager > Details > java.exe");
		System.out.println("(press ENTER to stop)");
		System.console().readLine();
		System.out.println(":: DONE ::");
	}
}