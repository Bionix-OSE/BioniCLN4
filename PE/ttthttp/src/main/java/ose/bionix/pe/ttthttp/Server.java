// Server.java
// Gets bringed up when the package is ran as "tttpds.java server"
// This is the HTTP-based version. Similar to the other, PDS version, the client holds the game state, and the server progresses the game for the client based entirely on the data sent by it - Making it stateless as such.

package ose.bionix.pe.ttthttp;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {
	private static final int PORT = 8080;
	// Game state status codes we will use in our response to the client.
	public static final String status_CONTINUE = "PLY";
	public static final String status_END = "END";

	public static void main() {
		try {
			// Initialize HttpServer
			HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
			// Map GameHandler to the single /game API path we provide
			server.createContext("/game", new GameHandler());
			server.setExecutor(null); // Creates a default executor
			System.out.println("Server running on port " + PORT + " at /game");
			server.start();
		} catch (IOException e) {
			System.err.println("ERROR: Failed to start HttpServer - " + e.getMessage());
		}
	}

	private static class GameHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			Board board = new Board();
			String boardStr = board.serialize(); // Empty board for new game (no request params)

			// Read client request
			boolean isNewGame = "GET".equalsIgnoreCase(exchange.getRequestMethod());
			if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
				// Parse request URL
				Map<String, String> params = parseRequestBody(exchange.getRequestBody());
				if (params.containsKey("boardStr")) {
					boardStr = params.get("boardStr");
				}
			}

			// Game mechanics logic
			if (!isNewGame && boardStr != null && !boardStr.isBlank()) {
				board.deserialize(boardStr); // Update the board with the one sent by client
			}
			int win = board.checkWin(); // Immediately do a win check first
			if (!isNewGame && win == Board.EMPTY) {
				// Then if human didn't win, let CPU do its move
				Server_CPUPlayer cpu = new Server_CPUPlayer(Board.O);
				cpu.chooseMove(board);
				win = board.checkWin(); // and check again to see if the CPU won
			}

			// Formulate data to send back to the client
			String status = (win == Board.EMPTY) ? status_CONTINUE : status_END;
			String boardStrNew = board.serialize();
			// Construct new response from said data
			String responseJSON = String.format(
				"{\"status\":\"%s\", \"board\":\"%s\", \"winner\":%d}",
				status, boardStrNew, win
			);
			// And now we send it back
			byte[] responseBytes = responseJSON.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
			exchange.sendResponseHeaders(200, responseBytes.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(responseBytes);
			}
		}

		// Request URL parameters -> Hashtable helper
		private Map<String, String> parseRequestBody(InputStream is) throws IOException {
			Map<String, String> result = new HashMap<>();
			String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			if (body.isBlank()) return result;
			String[] pairs = body.split("&");
			for (String pair : pairs) {
				String[] kv = pair.split("=");
				if (kv.length == 2) {
					String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
					String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
					result.put(key, value);
				}
			}
			return result;
		}
	}
}