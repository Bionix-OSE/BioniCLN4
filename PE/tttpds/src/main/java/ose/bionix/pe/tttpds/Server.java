package ose.bionix.pe.tttpds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
	private static final int PORT = 8080;
	private static final String request_NEWGAME = "NEW";
	private static final String request_UPDATE = "UPD";
	private static final String status_CONTINUE = "PLY";
	private static final String status_END = "END";

	public static void main() {
		try (ServerSocket ssock = new ServerSocket(PORT)) {
			while (true) {
				try (Socket csock = ssock.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(csock.getInputStream()));
					PrintWriter out = new PrintWriter(csock.getOutputStream(), true)) {
						// Read client request
						String reqBuffer = in.readLine();
						if (reqBuffer == null || (reqBuffer.substring(2,3) == ";")) continue;

						// Parse request
						Board board = new Board();
						String boardNew, status; int win = Board.EMPTY;
						String[] request = reqBuffer.split(";");
						switch (request.length) {
							case 1: { // NEW
								if (request[0] == request_NEWGAME) {
									boardNew = board.serialize();
									status = status_CONTINUE;
									break;
								} else {continue;}
							}
							case 2: { // UPD
								if (request[0] == request_UPDATE) {
									board.deserialize(request[1]);
									win = board.checkWin();
									if (win == Board.EMPTY) {
										Server_CPUPlayer cpu = new Server_CPUPlayer(Board.O);
										cpu.chooseMove(board);
										win = board.checkWin(); // Check again in case if CPU's move is the last move
									}
									boardNew = board.serialize();
									status = List.of(Board.X,Board.O,Board.DRAW).contains(win) ? status_END : status_CONTINUE;
									break;
								} else {continue;}
							}
							default: {continue;}
						}

						// Construct new response back to client
						// Format: STATUS;BOARDNEW;WIN
						// e.g.: PLY;000000000;0 | END;001001001;1 | END;021021021;3
						String response = status + ";" + boardNew + ";" + win;
						out.println(response);
					} catch (Exception e) {System.err.println("Request processing error: " + e.getMessage());}
			}
		} catch (Exception e) {
			System.err.println("Server critical error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
