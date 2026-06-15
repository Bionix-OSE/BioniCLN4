package ose.bionix.pe.tttnai;

import java.io.IOException;

/**
 * Main entry point for Tic-Tac-Toe Network (TTTNet) application.
 * 
 * Supports two modes:
 * 1. Server mode: Starts a server that accepts client connections
 * 2. Client mode: Connects to a server and plays a game
 * 
 * Usage:
 *   java Main server                          // Start server on port 5555
 *   java Main client [host] [port]           // Connect to server
 *   java Main client localhost 5555          // Connect to local server
 */
public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			printUsage();
			return;
		}

		String mode = args[0].toLowerCase();

		try {
			if ("server".equals(mode)) {
				startServer();
			} else if ("client".equals(mode)) {
				startClient(args);
			} else {
				System.err.println("Unknown mode: " + mode);
				printUsage();
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Start the game server
	 */
	private static void startServer() {
		System.out.println("Starting Tic-Tac-Toe server...");
		GameServer server = new GameServer();
		server.start();
	}

	/**
	 * Start the game client
	 */
	private static void startClient(String[] args) throws IOException {
		String host = "localhost";
		int port = 5555;

		// Parse optional host and port arguments
		if (args.length >= 2) {
			host = args[1];
		}
		if (args.length >= 3) {
			try {
				port = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("Invalid port number: " + args[2]);
				System.exit(1);
			}
		}

		System.out.println("Connecting to Tic-Tac-Toe server at " + host + ":" + port + "...");
		GameClient client = new GameClient(host, port);
		try {
			client.connect();
			client.play();
		} catch (IOException e) {
			System.err.println("Connection error: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Print usage information
	 */
	private static void printUsage() {
		System.out.println("Usage: java Main [server|client]");
		System.out.println();
		System.out.println("Modes:");
		System.out.println("  server                     - Start the game server (listens on port 5555)");
		System.out.println("  client [host] [port]       - Connect as a client");
		System.out.println();
		System.out.println("Examples:");
		System.out.println("  java Main server");
		System.out.println("  java Main client");
		System.out.println("  java Main client localhost 5555");
		System.out.println("  java Main client 192.168.1.100 5555");
	}
}
