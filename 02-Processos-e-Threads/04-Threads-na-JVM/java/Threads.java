public class Threads {
	public static void main(String[] args) throws Exception {

		final Thread th1 = new Thread(() -> {
			try {
				System.out.println("[T1] :: Starting ::");
				for (int i = 0; i < 8; ++i) {
					Thread.sleep(800);
					System.out.println("[T1] :: >> " + i + " << ::");
				}
				Thread.sleep(800);
				System.out.println("[T1] :: Done ::");
			} catch (InterruptedException ie) {
				System.err.println("[T1] :: Interrupted ::");
			}
		});

		final Thread th2 = new Thread(() -> {
			try {
				System.out.println("[T2] :: Starting ::");
				for (int i = 0; i < 8; ++i) {
					Thread.sleep(1200);
					System.out.println("[T2] :: >> " + i + " << ::");
				}
				Thread.sleep(1200);
				System.out.println("[T2] :: Done ::");
			} catch (InterruptedException ie) {
				System.err.println("[T2] :: Interrupted ::");
			}
		});

		System.out.println("[MN] :: Starting ::");
		th1.start();
		th2.start();

		System.out.println("[MN] :: Waiting ::");
		th1.join();
		th2.join();

		System.out.println("[MN] :: Done ::");
		return;
	}
}
