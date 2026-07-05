// Server.java
// Gets bringed up when the package is ran as "tttpds.java server"
// This is the "Protocol Defined Socket (PDS)" version, where the server and client communicates over socket, but uses a communication protocol (fancy way of saying request/response format) I defined myself to progress the game.
// The client holds the game state, and the server progresses the game for the client based entirely on the data sent by it. As such, the server is stateless. You can think of this as the other, HTTP-based version, but oversimplified.
// I chose to do this instead of the classic "server runs the game" method for two reasons:
// 1. This easily satisfies the "single-threaded, multi-user" problem: The request-reponse based system allows the single server thread to handle requests and send responses from and to any amount of clients at the speed of the CPU. There is no "session" on the server to begin with, that lives on the client side.
// 2. Allowed me to reuse code for the HTTP-based version later on.

package ose.bionix.pe.tttpds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
	private static final int PORT = 8080;
	// These two are the request codes used by the client
	public static final String request_NEWGAME = "NEW";
	public static final String request_UPDATE = "UPD";
	// And these two are the response codes we will use to reply back to it
	public static final String status_CONTINUE = "PLY";
	public static final String status_END = "END";

	public static void main() {
		// Initialize socket-based server
		try (ServerSocket ssock = new ServerSocket(PORT)) {
			System.out.println("Server running on port " + PORT);
			while (true) {
				try (Socket csock = ssock.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(csock.getInputStream()));
					PrintWriter out = new PrintWriter(csock.getOutputStream(), true)) {
						Board board = new Board();
						String boardNew, status; int win = Board.EMPTY;

						// Read client request
						String reqBuffer = in.readLine();
						if (reqBuffer == null || reqBuffer.isBlank()) continue;
						// Parse request
						String[] request = reqBuffer.split(";");
						String command = request[0];
						if (request_NEWGAME.equals(command)) { // "NEW"
							boardNew = board.serialize(); // Empty board for new game
							status = status_CONTINUE;
						}
						else if (request_UPDATE.equals(command)) { // "UPD"
							board.deserialize(request[1]); // Update the board with the one sent by client
							win = board.checkWin();
							if (win == Board.EMPTY) { // Immediately do a win check first
								// Then if human didn't win, let CPU do its move
								Server_CPUPlayer cpu = new Server_CPUPlayer(Board.O);
								cpu.chooseMove(board);
								win = board.checkWin(); // and check again to see if the CPU won
							}
							// Formulate data to send back to the client
							boardNew = board.serialize();
							status = List.of(Board.X,Board.O,Board.DRAW).contains(win) ? status_END : status_CONTINUE;
						} else {continue;}

						// Construct new response from said data
						// Format: STATUS;BOARDNEW;WIN
						// e.g.: PLY;000000000;0 | END;001001001;1 | END;021021021;3
						String response = status + ";" + boardNew + ";" + win;
						// And now we send it back
						out.println(response);
				} catch (Exception e) {System.err.println("Request processing error: " + e.getMessage());}
			}
		} catch (Exception e) {
			System.err.println("Server critical error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
