package ose.bionix.pe.tttnai;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Tic-Tac-Toe Game Client
 * 
 * Connects to a game server and plays a game against the computer.
 * The client can be controlled via command line or standard input.
 * 
 * Communication protocol:
 * - Server → Client: WELCOME:message
 * - Server → Client: GAME_START:message
 * - Server → Client: BOARD:state|TURN:playerNum
 * - Server → Client: ERROR:message
 * - Server → Client: END:message
 * - Client → Server: MOVE:position (0-8)
 * - Client → Server: QUIT
 */
public class GameClient {
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 5555;
	
	private String host;
	private int port;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Scanner userInput;
	private volatile boolean gameActive;
	private String lastBoardState;

	public GameClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.userInput = new Scanner(System.in);
		this.gameActive = false;
		this.lastBoardState = "";
	}

	public GameClient() {
		this(DEFAULT_HOST, DEFAULT_PORT);
	}

	/**
	 * Connect to the server
	 */
	public void connect() throws IOException {
		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("[Client] Connected to server at " + host + ":" + port);

			// Receive welcome message
			String welcome = in.readLine();
			if (welcome != null) {
				if (welcome.startsWith("WELCOME:")) {
					System.out.println("[Server] " + welcome.substring(8));
				}
			}
		} catch (IOException e) {
			throw new IOException("Failed to connect to server at " + host + ":" + port + " - " + e.getMessage());
		}
	}

	/**
	 * Main game loop
	 */
	public void play() {
		try {
			gameActive = true;

			// Start listener thread for server messages
			Thread listenerThread = new Thread(this::listenForMessages, "MessageListener");
			listenerThread.setDaemon(true);
			listenerThread.start();

			// Main loop for sending moves
			while (gameActive) {
				System.out.print("\n> Enter position [1-9], [s]tatus, or [q]uit: ");
				String input = userInput.nextLine().trim().toLowerCase();

				if (input.isEmpty()) {
					continue;
				}

				switch (input) {
					case "q":
					case "quit":
						out.println("QUIT");
						out.flush();
						gameActive = false;
						break;
					
					case "s":
					case "status":
						displayBoard(lastBoardState);
						break;
					
					default:
						// Try to parse as position
						try {
							int position = Integer.parseInt(input);
							if (position < 1 || position > 9) {
								System.out.println("  ERROR: Position must be between 1 and 9");
								continue;
							}
							int zeroBasedPos = position - 1;
							out.println("MOVE:" + zeroBasedPos);
							out.flush();
						} catch (NumberFormatException e) {
							System.out.println("  ERROR: Invalid input. Enter a number 1-9, 's' for status, or 'q' to quit");
						}
				}
			}

			// Wait for listener thread to finish
			listenerThread.join(2000);

		} catch (InterruptedException e) {
			System.out.println("\n[Client] Interrupted");
			Thread.currentThread().interrupt();
		} finally {
			cleanup();
		}
	}

	/**
	 * Listen for messages from server in a separate thread
	 */
	private void listenForMessages() {
		try {
			String line;
			while ((line = in.readLine()) != null && gameActive) {
				System.out.println();  // New line before message

				if (line.startsWith("GAME_START:")) {
					System.out.println("[Server] " + line.substring(11));
				} 
				else if (line.startsWith("BOARD:")) {
					handleBoardUpdate(line);
				} 
				else if (line.startsWith("ERROR:")) {
					System.out.println("[ERROR] " + line.substring(6));
				} 
				else if (line.startsWith("END:")) {
					String endMessage = line.substring(4);
					displayMessage("=== GAME OVER ===");
					displayMessage(endMessage);
					displayMessage("==================");
					gameActive = false;
					break;
				} 
				else if (line.startsWith("WELCOME:")) {
					// Already handled during connect
				}
				else {
					System.out.println("[Server] " + line);
				}
			}
		} catch (IOException e) {
			if (gameActive) {
				System.err.println("\n[Client] Connection lost: " + e.getMessage());
				gameActive = false;
			}
		}
	}

	/**
	 * Handle board state update from server
	 */
	private void handleBoardUpdate(String line) {
		// Parse: BOARD:cells|TURN:playerNum
		String[] parts = line.split("\\|");
		if (parts.length >= 2) {
			String boardStr = parts[0].substring(6); // Remove "BOARD:"
			String turnStr = parts[1].substring(5); // Remove "TURN:"
			
			try {
				int turn = Integer.parseInt(turnStr);
				lastBoardState = boardStr;
				
				System.out.println();
				displayBoard(boardStr);

				if (turn == 1) {
					System.out.println("\n>>> YOUR TURN - Enter position [1-9]");
				} else {
					System.out.println("\n... COMPUTER IS THINKING ...");
				}
			} catch (NumberFormatException e) {
				System.err.println("[ERROR] Invalid board state from server");
			}
		}
	}

	/**
	 * Display the game board
	 */
	private void displayBoard(String boardStr) {
		String[] cells = boardStr.split(",");
		if (cells.length != 9) {
			System.err.println("[ERROR] Invalid board state");
			return;
		}

		System.out.println();
		System.out.println("+---+---+---+");
		System.out.println("| " + cells[0] + " | " + cells[1] + " | " + cells[2] + " |");
		System.out.println("+---+---+---+");
		System.out.println("| " + cells[3] + " | " + cells[4] + " | " + cells[5] + " |");
		System.out.println("+---+---+---+");
		System.out.println("| " + cells[6] + " | " + cells[7] + " | " + cells[8] + " |");
		System.out.println("+---+---+---+");
	}

	/**
	 * Display a message
	 */
	private void displayMessage(String message) {
		System.out.println(message);
	}

	/**
	 * Cleanup resources
	 */
	private void cleanup() {
		try {
			gameActive = false;
			if (socket != null && !socket.isClosed()) {
				socket.close();
				System.out.println("[Client] Disconnected");
			}
		} catch (IOException e) {
			System.err.println("[Client] Error during cleanup: " + e.getMessage());
		}
	}

	/**
	 * Entry point for running as standalone application
	 */
	public static void main(String[] args) {
		String host = DEFAULT_HOST;
		int port = DEFAULT_PORT;

		// Parse command line arguments
		if (args.length >= 1) {
			host = args[0];
		}
		if (args.length >= 2) {
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Invalid port number: " + args[1]);
				System.exit(1);
			}
		}

		GameClient client = new GameClient(host, port);
		try {
			client.connect();
			client.play();
		} catch (IOException e) {
			System.err.println("[Client] Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
