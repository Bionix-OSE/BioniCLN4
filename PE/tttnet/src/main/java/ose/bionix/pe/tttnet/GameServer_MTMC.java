package ose.bionix.pe.tttnet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer_MTMC {
	private static final int port = 8080;
	private final ExecutorService pool;
	public int firstPlayer = 1;

	public GameServer_MTMC() {
		this.pool = Executors.newCachedThreadPool();
	}

	public void run() {
		System.out.println("Server starting on port " + port + "...");
		try (ServerSocket srv = new ServerSocket(port)) {
			System.out.println("Waiting for clients to connect...");
			while (true) {
				Socket client = srv.accept();
				System.out.println("Client connected from " + client.getRemoteSocketAddress());
				pool.execute(new ClientSession(client, firstPlayer));
			}
		} catch (Exception e) {
			System.err.println("ERROR: Server exception encountered: " + e.getMessage());
			e.printStackTrace();
		} finally {
			pool.shutdown();
		}
	}

	private static class ClientSession implements Runnable {
		private final Socket client;
		private final int firstPlayer;

		public ClientSession(Socket client, int firstPlayer) {
			this.client = client;
			this.firstPlayer = firstPlayer;
		}

		@Override
		public void run() {
			try (Socket socket = client;
				 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

				Board board = new Board(out);
				HumanPlayer1 p1 = new HumanPlayer1(Board.X, in, out);
				CPUPlayer p2 = new CPUPlayer(Board.O);
				Game game = new Game(board, p1, p2, out);
				game.firstPlayer = firstPlayer;

				game.run();
				System.out.println("INFO: Game over. Closing client socket.");
			} catch (Exception e) {
				System.err.println("ERROR: Client session exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
