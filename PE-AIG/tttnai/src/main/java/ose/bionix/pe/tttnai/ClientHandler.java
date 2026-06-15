package ose.bionix.pe.tttnai;

import java.io.*;
import java.net.*;

/**
 * Handles a single client connection.
 * Manages communication between client and its GameInstance.
 * 
 * Architecture:
 * - Main thread: Listens for client messages (moves, quit commands)
 * - Game thread: Runs the game loop and sends state updates
 */
public class ClientHandler implements Runnable {
	private static final long SHUTDOWN_TIMEOUT = 5000; // 5 seconds
	
	private Socket socket;
	private int clientNumber;
	private PrintWriter out;
	private BufferedReader in;
	private GameInstance game;

	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
	}

	@Override
	public void run() {
		try {
			// Setup I/O streams
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("[ClientHandler-" + clientNumber + "] I/O streams initialized");

			// Send welcome message
			out.println("WELCOME:Connected to Tic-Tac-Toe server");
			out.flush();

			// Create game instance for this client
			game = new GameInstance(out);
			System.out.println("[ClientHandler-" + clientNumber + "] GameInstance created");

			// Start game in separate thread (it will run independently)
			Thread gameThread = new Thread(game, "GameThread-" + clientNumber);
			gameThread.setDaemon(false); // Game thread won't be a daemon
			gameThread.start();
			System.out.println("[ClientHandler-" + clientNumber + "] Game thread started");

			// Main loop: Listen for moves from the client
			String line;
			while ((line = in.readLine()) != null && game.isActive()) {
				System.out.println("[ClientHandler-" + clientNumber + "] Received: " + line);

				if (line.startsWith("MOVE:")) {
					try {
						String posStr = line.substring(5);
						int position = Integer.parseInt(posStr);
						game.submitMove(position);
					} catch (NumberFormatException e) {
						System.out.println("[ClientHandler-" + clientNumber + "] Invalid move format: " + line);
						out.println("ERROR:Invalid move format. Use MOVE:0 to MOVE:8");
						out.flush();
					}
				} else if (line.equals("QUIT")) {
					System.out.println("[ClientHandler-" + clientNumber + "] Client requested quit");
					game.end();
					break;
				} else if (line.startsWith("DEBUG:")) {
					System.out.println("[ClientHandler-" + clientNumber + "] DEBUG: " + line.substring(6));
				} else {
					System.out.println("[ClientHandler-" + clientNumber + "] Unknown message: " + line);
				}
			}

			// Wait for game thread to finish (with timeout)
			if (gameThread.isAlive()) {
				System.out.println("[ClientHandler-" + clientNumber + "] Waiting for game thread to finish...");
				gameThread.join(SHUTDOWN_TIMEOUT);
				
				if (gameThread.isAlive()) {
					System.out.println("[ClientHandler-" + clientNumber + "] WARNING: Game thread did not finish in time");
					gameThread.interrupt();
					gameThread.join(1000);
				}
			}

			System.out.println("[ClientHandler-" + clientNumber + "] Game session ended");

		} catch (IOException e) {
			System.err.println("[ClientHandler-" + clientNumber + "] I/O Error: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("[ClientHandler-" + clientNumber + "] Interrupted: " + e.getMessage());
			Thread.currentThread().interrupt();
		} finally {
			cleanup();
		}
	}

	/**
	 * Cleanup resources on disconnect
	 */
	private void cleanup() {
		try {
			if (game != null) {
				game.end();
			}
			if (socket != null && !socket.isClosed()) {
				socket.close();
				System.out.println("[ClientHandler-" + clientNumber + "] Socket closed");
			}
		} catch (IOException e) {
			System.err.println("[ClientHandler-" + clientNumber + "] Error during cleanup: " + e.getMessage());
		}
	}
}
