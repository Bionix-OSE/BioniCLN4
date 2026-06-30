package ose.bionix.pe.tttnet;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer_STSC {
	private static final int port = 8080;
	public int firstPlayer = 1;

	public GameServer_STSC() {}

	public void run() {
		System.out.println("Server starting on port " + port + "...");
		try (ServerSocket srv = new ServerSocket(port)) {
			System.out.println("Waiting for player to connect...");
			Socket client = srv.accept();
			System.out.println("Player connected from " + client.getRemoteSocketAddress());

			BufferedReader in = new BufferedReader(new java.io.InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			Board board = new Board(out);
			HumanPlayer p1 = new HumanPlayer(Board.X, in, out);
			CPUPlayer p2 = new CPUPlayer(Board.O);
			Game game = new Game(board, p1, p2, out);
			game.firstPlayer = firstPlayer;
			Thread gameThread = new Thread(game);
			gameThread.start();
			gameThread.join(); // Wait for the game to finish before cleaning up

			System.out.println("INFO: Game over. Closing client socket.");
			client.close();
		} catch (Exception e) {
			System.err.println("ERROR: Server exception encountered: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
