import java.util.Arrays;

class Seats {
	private int availableSeats = 20;
	private boolean bookSuccessful = false;

	public void bookSeat() {
		bookSuccessful = false;
		if (availableSeats > 8) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			availableSeats-=8;
			bookSuccessful = true;
		}
	}
	public int getAvailableSeats() {
		return availableSeats;
	}
	public boolean getStatus() {
		return bookSuccessful;
	}
}

class Book extends Thread {
	private int id;
	private Seats seats;
	public Book(Seats s) {
		seats = s;
	}

	@Override
	public void run()
	{
		System.out.println("BookingThread-" + id + " requests 8 seats.");
		seats.bookSeat();
		int availableSeats = seats.getAvailableSeats();
		System.out.println("BookingThread-" + id + " booking " + (seats.getStatus() ? "successful" : "failed") + ". Remaining seats: " + availableSeats);
	}
}

public class SMTTicket_Unsafe {
	public static void main(String[] args) {
		Seats s = new Seats();

		Book b1 = new Book(s);
		Book b2 = new Book(s);
		Book b3 = new Book(s);
		try {
			b1.start(); b2.start(); b3.start();
			b1.join(); b2.join(); b3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
