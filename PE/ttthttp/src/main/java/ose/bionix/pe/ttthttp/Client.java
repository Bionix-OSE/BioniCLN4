// Client.java: Main code for the client
// This is what faces the user when they run "ttthttp.jar client"

package ose.bionix.pe.ttthttp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Client {
	private final String host;
	private final int port;
	private final Board board;
	private final HttpClient http;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
		this.board = new Board();
		this.http = HttpClient.newHttpClient();
	}

	// Display functions
	// These facilitates displaying the board in the terminal
	private int getCell(int idx) {
		String boardserialized = board.serialize();
		return Character.getNumericValue(boardserialized.charAt(idx));
	}
	private void display(PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int r = 0; r < 3; r++) { // This builds a cell |-|
			sb.append("|");
			for (int c = 0; c < 3; c++) {
				int idx = r * 3 + c;
				String v = switch (getCell(idx)) {
					default -> "-";
					case Board.X -> "X";
					case Board.O -> "O";
				};
				sb.append(v);
				if (c < 2) sb.append("|");
			}
			sb.append(" |");
			if (r < 2) sb.append('\n');
		}
		out.println(sb.toString());
		// End result looks like this:
		// |-||-||-|
		// |-||-||-|
		// |-||-||-|
	}

	// Communication functions
	private String sendGet() {
		// Faciliates sending GET to the server
		// In this version, GET means new game, and the client expects and empty board back (it doesn't store any board at first).
		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + host + ":" + port + "/game"))
				.GET()
				.build();
			HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return response.body();
			}
		} catch (Exception e) {
			System.err.println("ERROR: GET Request failed - " + e.getMessage());
		}
		return null;
	}
	private String sendPost(String boardStr) {
		// Faciliates sending POST to the server
		// In this version, POST is how the client updates the game state with the server. Only 1 parameter is sent, which is the game board. Any game progression decision is in the server response to this specific request.
		// The server receive the board, modifies it with its CPU move, does win checking, sends result back, and doesn't remember a thing.
		try {
			String formBody = "boardStr=" + URLEncoder.encode(boardStr, StandardCharsets.UTF_8);
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + host + ":" + port + "/game"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(formBody))
				.build();
			HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return response.body();
			}
		} catch (Exception e) {
			System.err.println("ERROR: POST Request failed - " + e.getMessage());
		}
		return null;
	}
	private String getJsonValue(String json, String key) {
		// JSON '{ key: "value" } -> String value' helper
		String pattern = "\"" + key + "\":";
		int start = json.indexOf(pattern);
		if (start == -1) return "";
		start += pattern.length();
		while (start < json.length() && (Character.isWhitespace(json.charAt(start)) || json.charAt(start) == '"')) {
			start++;
		}
		int end = start;
		while (end < json.length() && json.charAt(end) != '"' && json.charAt(end) != ',' && json.charAt(end) != '}') {
			end++;
		}
		return json.substring(start, end).trim();
	}
	private String[] parseResponse(String json) {
		// Populates the server's JSON response to the corresponding variables that we can read from in the gameplay logic below.
		if (json == null || !json.contains("status")) return null;
		String status = getJsonValue(json, "status");
		String boardStr = getJsonValue(json, "board");
		String winner = getJsonValue(json, "winner");
		// Update the local board state as well
		board.deserialize(boardStr);
		return new String[] { status, boardStr, winner };
	}

	// Speaking of gameplay logic, here it is
	public void play() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(System.out, true)) {
			Client_HumanPlayer player = new Client_HumanPlayer(Board.X, in, out);

			out.println("INFO: Connecting to server...");
			String response = sendGet(); // New game
			String[] resparray = parseResponse(response);
			if (resparray == null || !resparray[0].equals(Server.status_CONTINUE)) {
				out.println("ERROR: Could not initialize match with server.");
				return;
			}
			out.println("GAME: Begin.");
			display(out);

			while (true) {
				// Let player do their move
				if (!player.chooseMove(board)) {
					out.println("Game over.");
					break;
				}

				// Serialize the board with the new move, then send it to the server, and wait for its response
				response = sendPost(board.serialize());
				if (response == null) {
					out.println("ERROR: Lost connection to server.");
					break;
				}
				// Once we got a valid response, parse it (this will also update the local board)
				resparray = parseResponse(response);
				if (resparray == null) {
					out.println("ERROR: Bad server response.");
					break;
				}
				
				// Do checks with the parsed response:
				String status = resparray[0]; // Game status (playing/ended?)
				int win = Integer.parseInt(resparray[2]); // Who won or draw? (1/2/3 for X/O/Draw)
				// Display the board, then end the game based on status & win (or restart the loop for the next move)
				display(out);
				if (Server.status_END.equals(status)) {
					out.println(win == 3 ? "GAME: It's a draw!" : "GAME: Player " + (win == 1 ? "X" : "O") + " won!");
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR: Critical client error - " + e.getMessage());
			e.printStackTrace();
		}
	}
}
