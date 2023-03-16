public class Counter {
	private int value;
	
	public Counter(int initial) {
		value = initial;
	}
	
	public synchronized int increment()  {
		return ++value;
	}
	
	public synchronized int decrement()  {
		return --value;
	}
	
	public synchronized int get() {
		return value;
	}

	// ==================

	private static int MAX_N = 10000000;

	private static Counter counter = new Counter(0);

	private static void action() {
		for (int n = 1; n <= MAX_N; ++n) {
			counter.increment();
		}
	}

	public static void main(String[] args) throws Exception {
		final var th1 = new Thread(() -> { action(); });
		final var th2 = new Thread(() -> { action(); });
		
		System.out.println(":: STARTING ::");
		
		th1.start();
		th2.start();
		
		System.out.println(":: WAITING ::");
		
		th1.join();
		th2.join();
		
		System.out.println(":: RESULTS ::");
		
		System.out.printf("counter: %d\n", counter.get());
	}
}