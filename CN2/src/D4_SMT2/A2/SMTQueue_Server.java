import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

class Request {
	Socket socket;
	String message;
	public Request(Socket socket, String message) {
		this.socket = socket;
		this.message = message;
	}
}

class ClientHandler implements Runnable {
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = in.readLine();
			if (line != null) {
				SMTQueue_Server.queue.put(new Request(socket, line));
			} else {
				socket.close();
			}
		} catch (Exception e) {
			System.out.println("ClientHandler error: " + e.getMessage());
			try { socket.close(); } catch (Exception ex) {}
		}
	}
}

public class SMTQueue_Server {
	public static BlockingQueue<Request> queue = new LinkedBlockingQueue<>(); // list of Client Record
	public static ExecutorService pool1;
	public static ExecutorService pool2;

	public static void main(String[] args) throws Exception {
		queue = new LinkedBlockingQueue<>();
		pool1 = Executors.newFixedThreadPool(5);
		pool2 = Executors.newFixedThreadPool(5);
		ServerSocket ss = new ServerSocket(1234);

		// start worker threads once (consume requests from queue)
		for (int i = 0; i < 5; i++) {
			pool2.execute(() -> {
				while (true) {
					try {
						Request req = queue.take();
						PrintWriter out = new PrintWriter(req.socket.getOutputStream(), true);
						out.println("Processed: " + req.message.toUpperCase());
						req.socket.close();
					} catch (Exception e) {
						System.out.println("Worker error: " + e.getMessage());
					}
				}
			});
		}

		while (true) {
			Socket clientSocket = ss.accept();
			pool1.execute(new ClientHandler(clientSocket));
		}
	}
}
