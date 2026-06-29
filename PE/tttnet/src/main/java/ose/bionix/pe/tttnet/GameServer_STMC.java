package ose.bionix.pe.tttnet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class GameServer_STMC {
	private static final int PORT = 8080;
	private static final int BUFFER_SIZE = 1024;

	private final Selector selector;
	private final ServerSocketChannel serverChannel;

	public GameServer_STMC() throws IOException {
		this.selector = Selector.open();
		this.serverChannel = ServerSocketChannel.open();
		this.serverChannel.bind(new InetSocketAddress(PORT));
		this.serverChannel.configureBlocking(false);
		this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void run() {
		System.out.println("STMC server listening on port " + PORT + "...");
		try {
			while (true) {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();

				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();

					if (!key.isValid()) {
						continue;
					}

					if (key.isAcceptable()) {
						acceptClient();
					} else if (key.isReadable()) {
						handleClientRead(key);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("ERROR: Server exception encountered: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void acceptClient() throws IOException {
		SocketChannel clientChannel = serverChannel.accept();
		if (clientChannel == null) {
			return;
		}

		clientChannel.configureBlocking(false);
		Socket socket = clientChannel.socket();
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		ClientSession session = new ClientSession(clientChannel, out);
		session.initializeGame();
		clientChannel.register(selector, SelectionKey.OP_READ, session);
		System.out.println("Client connected from " + clientChannel.getRemoteAddress());
	}

	private void handleClientRead(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ClientSession session = (ClientSession) key.attachment();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		int read = channel.read(buffer);

		if (read == -1) {
			System.out.println("Client disconnected: " + channel.getRemoteAddress());
			channel.close();
			key.cancel();
			return;
		}

		if (read == 0) {
			return;
		}

		buffer.flip();
		String incoming = StandardCharsets.UTF_8.decode(buffer).toString();
		session.input.append(incoming);

		String text = session.input.toString();
		int newline = text.indexOf('\n');
		if (newline >= 0) {
			String line = text.substring(0, newline).trim();
			session.input.delete(0, newline + 1);
			processCommand(session, line);
		}
	}

	private void processCommand(ClientSession session, String line) throws IOException {
		if (line.isBlank()) {
			return;
		}

		if ("quit".equalsIgnoreCase(line)) {
			session.send("Goodbye!\n");
			session.close();
			return;
		}

		if (line.matches("\\d+")) {
			int move = Integer.parseInt(line);
			if (move < 1 || move > 9) {
				session.send("Please enter a number between 1 and 9.\n");
				return;
			}
			session.submitMove(move);
			return;
		}

		session.send("Unknown command. Try a number 1-9 or 'quit'.\n");
	}

	public static class ClientSession {
		private final SocketChannel channel;
		private final PrintWriter output;
		private final StringBuilder input = new StringBuilder();
		private Board board;
		private Game game;
		private HumanPlayer2 HumanPlayer2;
		private Integer pendingMove;

		private ClientSession(SocketChannel channel, PrintWriter output) {
			this.channel = channel;
			this.output = output;
		}

		private void initializeGame() {
			board = new Board(output);
			HumanPlayer2 = new HumanPlayer2(Board.X, this);
			CPUPlayer cpuPlayer = new CPUPlayer(Board.O);
			game = new Game(board, HumanPlayer2, cpuPlayer, output);
			game.firstPlayer = 1;
			output.println("INFO: Hello!");
			board.display();
			output.println("Input a position [1-9]: ");
			output.flush();
		}

		private void submitMove(int move) {
			if (game == null) {
				initializeGame();
			}
			pendingMove = move;
			if (!game.playNextTurn()) {
				close();
				return;
			}
			if (!game.isFinished()) {
				game.playNextTurn();
			}
			if (game.isFinished()) {
				close();
			}
		}

		public int takePendingMove() {
			if (pendingMove == null) {
				return -1;
			}
			int move = pendingMove;
			pendingMove = null;
			return move;
		}

		public void send(String message) {
			output.println(message);
			output.flush();
		}

		private void close() {
			try {
				channel.close();
			} catch (IOException e) {
				System.err.println("ERROR: Could not close client connection: " + e.getMessage());
			}
		}
	}
}

