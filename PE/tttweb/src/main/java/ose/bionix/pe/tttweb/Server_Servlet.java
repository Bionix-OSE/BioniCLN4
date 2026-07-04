package ose.bionix.pe.tttweb;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/game")
public class Server_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// GET = New game (empty board "000000000")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String boardStr = new String(new char[9]).replace("\0", "0");
		doGame(request, response, true, boardStr, -1);
	}

	// POST = Update game based on board state
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String boardStr = request.getParameter("boardStr");
		String humanMoveStr = request.getParameter("humanMove");
		int humanMove = -1;
		if (humanMoveStr != null) {
			humanMove = Integer.parseInt(humanMoveStr.trim());
		}
		doGame(request, response, false, boardStr, humanMove);
	}

	// All processing and game logic is in here
	private void doGame(HttpServletRequest request, HttpServletResponse response, boolean isNewGame, String boardStr, int humanMove) throws IOException {
		// CORS workaround
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
			return; // Stop processing further game logic for preflight requests
		}
		// Set content type
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		// The game logic
		Board board = new Board();
		if (!isNewGame && boardStr != null && !boardStr.isBlank()) {
			// Construct the board to the format server side code can read
			// using logic we've written in the week 9 version
			board.deserialize(boardStr);
		}
		int winStatus = board.checkWin(); // Immmediately do a win check before...
		if (!isNewGame && humanMove >= 0 && humanMove < 9 && winStatus == Board.EMPTY) {
			// ...we place the moves
			// 1. Process Human Move (X)
			board.place(humanMove, Board.X);
			winStatus = board.checkWin();
			// 2. Process CPU Move (O) if game isn't finished
			if (winStatus == Board.EMPTY) {
				Server_CPUPlayer cpu = new Server_CPUPlayer(Board.O);
				cpu.chooseMove(board);
				winStatus = board.checkWin();
			}
		}

		// Convert the result back to a format we can send back to client
		String status = (winStatus == Board.EMPTY) ? "PLY" : "END";
		String boardStrNew = board.serialize();
		String responseJSON = String.format(
			"{\"status\":\"%s\", \"board\":\"%s\", \"winner\":%d}",
			status, boardStrNew, winStatus
		);

		// Respond with JSON payload back to the JavaScript AJAX thread
		out.print(responseJSON);
		out.flush();
	}
}
