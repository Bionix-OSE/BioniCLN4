import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class ClientHandler implements Runnable {
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String line = in.readLine();
			String response = "Server received: " + line;
			out.println(response. toUpperCase());
		} 
		catch (IOException e) {System.out.println("Client error: " + e.getMessage());} 
		finally {try { socket.close(); } catch (IOException e) {}}
	}
}
public class SMTPool_Server {
	public static void main(String[] args) throws Exception {
		ServerSocket ss = new ServerSocket(1234);
		ExecutorService pool = Executors.newFixedThreadPool(5);
		while (true) {
			Socket clientSocket = ss.accept();
			pool.execute(new ClientHandler(clientSocket));
		}
	}
}
