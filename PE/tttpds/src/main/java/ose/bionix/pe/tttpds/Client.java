// Client.java: Main code for the client
// This is what faces the user when they run "tttpds.jar client"

package ose.bionix.pe.tttpds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private final String host;
	private final int port;
	private final Board board;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
		this.board = new Board();
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
	private String sendRequest(String req) {
		// Facilitates sending data to the server
		try (Socket ssock = new Socket(host, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(ssock.getInputStream()));
			PrintWriter out = new PrintWriter(ssock.getOutputStream(), true)) {
				out.println(req);
				return in.readLine();
			} catch (Exception e) {return null;}
	}
	private String[] parseResponse(String res) {
		// And this reads the server's responses back, then delimit and populate the values into an array
		String[] response = res.split(";");
		if (response.length >= 2) {
			// Update the local board state as well
			board.deserialize(response[1]);
		}
		return response;
	}

	// Gameplay logic (NOT the game mechanics - that's server side)
	public void play() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(System.out, true)) {
			Client_HumanPlayer player = new Client_HumanPlayer(Board.X, in, out);

			out.println("INFO: Connecting to server...");
			String response = sendRequest(Server.request_NEWGAME + ";");
			if (response == null || !parseResponse(response)[0].equals(Server.status_CONTINUE)) {
				out.println("ERROR: Could not connect to the server.");
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
				response = sendRequest(Server.request_UPDATE + ";" + board.serialize());
				if (response == null) {
					out.println("ERROR: Lost connection to server.");
					break;
				}
				// Once we got a valid response, parse it (this will also update the local board)
				String[] resparray = parseResponse(response);

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
			System.err.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
