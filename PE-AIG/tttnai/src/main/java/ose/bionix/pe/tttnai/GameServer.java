package ose.bionix.pe.tttnai;

import java.io.*;
import java.net.*;

/**
 * Tic-Tac-Toe Game Server
 * 
 * Listens for client connections on a fixed port.
 * Each client gets its own game instance where they play against the computer.
 * 
 * Architecture:
 * - Main thread: Accepts incoming connections
 * - Client thread: Manages each connection via ClientHandler
 */
public class GameServer {
	private static final int PORT = 5555;
	private static final int BACKLOG = 10;
	private volatile boolean running;
	private ServerSocket serverSocket;
	private int clientCounter;

	public GameServer() {
		this.running = true;
		this.clientCounter = 0;
	}

	/**
	 * Start the server and accept client connections
	 */
	public void start() {
		try {
			serverSocket = new ServerSocket(PORT, BACKLOG);
			System.out.println("[GameServer] Started on port " + PORT);
			System.out.println("[GameServer] Waiting for client connections...");
			System.out.println("[GameServer] Press Ctrl+C to stop the server");

			// Accept client connections indefinitely
			while (running) {
				try {
					Socket clientSocket = serverSocket.accept();
					clientCounter++;
					int clientId = clientCounter;

					System.out.println("[GameServer] Client " + clientId + " connected from " 
						+ clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

					// Create handler and start in new thread
					ClientHandler handler = new ClientHandler(clientSocket, clientId);
					Thread clientThread = new Thread(handler, "ClientHandler-" + clientId);
					clientThread.start();

				} catch (SocketException e) {
					if (running) {
						System.err.println("[GameServer] Socket error: " + e.getMessage());
					}
				} catch (IOException e) {
					if (running) {
						System.err.println("[GameServer] I/O error accepting connection: " + e.getMessage());
					}
				}
			}

		} catch (IOException e) {
			System.err.println("[GameServer] Failed to start server on port " + PORT + ": " + e.getMessage());
			e.printStackTrace();
		} finally {
			shutdown();
		}
	}

	/**
	 * Gracefully shutdown the server
	 */
	public synchronized void shutdown() {
		running = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
				System.out.println("[GameServer] Server shutdown");
			}
		} catch (IOException e) {
			System.err.println("[GameServer] Error closing server socket: " + e.getMessage());
		}
	}

	/**
	 * Entry point for running as standalone application
	 */
	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.start();
	}
}
