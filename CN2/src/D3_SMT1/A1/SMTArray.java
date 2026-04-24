import java.util.Arrays;

class Compute extends Thread {
	private int id;
	private int[] array;
	private int result;
	public Compute(int id, int[] array) {
		this.id = id;
		this.array = array;
	}

	public String getRange() {
		int start = (id - 1) * 250 + 1;
		int end = id * 250;
		return start + " to " + end;
	}
	public int getResult() {
		return result;
	}
	@Override
	public void run() {
		int sum = 0;
		for (int num : array) {
			sum += num;
		}
		this.result = sum;
	}
}
class SMTArray {
	public static void main(String[] args) {
		// Create an array of 1000 integers
		int[] array = new int[1000];
		for (int i = 0; i < 1000; i++) {
			array[i] = i + 1;
		}

		// Split the array into 4 parts
		int[][] arrays = new int[4][];
		for (int i = 0; i < 4; i++) {
			arrays[i] = java.util.Arrays.copyOfRange(array, i * 250, (i + 1) * 250);
		}
		int[] array1 = arrays[0];
		int[] array2 = arrays[1];
		int[] array3 = arrays[2];
		int[] array4 = arrays[3];

		// Compute sum of each part in 4 separate threads
		Compute thread1 = new Compute(1, array1); thread1.start();
		Compute thread2 = new Compute(2, array2); thread2.start();
		Compute thread3 = new Compute(3, array3); thread3.start();
		Compute thread4 = new Compute(4, array4); thread4.start();
		try {
			thread1.join();
			thread2.join();
			thread3.join();
			thread4.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int sum1 = thread1.getResult(); String range1 = thread1.getRange();
		int sum2 = thread2.getResult(); String range2 = thread2.getRange();
		int sum3 = thread3.getResult(); String range3 = thread3.getRange();
		int sum4 = thread4.getResult(); String range4 = thread4.getRange();
		int totalSum = sum1 + sum2 + sum3 + sum4;

		System.out.println("Thread 1 processed index " + range1 + ", partial sum: " + sum1);
		System.out.println("Thread 2 processed index " + range2 + ", partial sum: " + sum2);
		System.out.println("Thread 3 processed index " + range3 + ", partial sum: " + sum3);
		System.out.println("Thread 4 processed index " + range4 + ", partial sum: " + sum4);
		System.out.println("Final total sum: " + totalSum);
	}
}
