package ose.bionix.pe.tttweb;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/game")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// GET requests start a fresh game
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		processGameRequest(request, response, true, -1);
	}

	// POST requests process a player's click action
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String posParam = request.getParameter("position");
		int position = -1;
		try {
			if (posParam != null) {
				position = Integer.parseInt(posParam.trim());
			}
		} catch (NumberFormatException e) {
			// Fallback for unexpected inputs
		}
		processGameRequest(request, response, false, position);
	}

	private void processGameRequest(HttpServletRequest request, HttpServletResponse response, boolean isNewGame, int playerMove) 
			throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		// Bind the board to the unique user session state
		HttpSession session = request.getSession(true);
		Board board = (Board) session.getAttribute("gameBoard");

		if (isNewGame || board == null) {
			board = new Board();
			session.setAttribute("gameBoard", board);
		}

		int winStatus = board.checkWin();

		// Process a move only if it's an ongoing match and the selected slot is legal
		if (!isNewGame && playerMove >= 0 && playerMove < 9 && winStatus == Board.EMPTY) {
			if (!board.isCellOccupied(playerMove)) {
				// 1. Process Human Move (X)
				board.place(playerMove, Board.X);
				winStatus = board.checkWin();

				// 2. Process AI Turn (O) if game isn't finished
				if (winStatus == Board.EMPTY) {
					Server_CPUPlayer cpu = new Server_CPUPlayer(Board.O);
					cpu.chooseMove(board);
					winStatus = board.checkWin();
				}
			}
		}

		String statusFlag = (winStatus == Board.EMPTY) ? "PLY" : "END";
		String serializedBoard = board.serialize();

		// Output raw JSON payload back to the JavaScript AJAX thread
		String jsonResponse = String.format(
			"{\"status\":\"%s\", \"board\":\"%s\", \"winner\":%d}",
			statusFlag, serializedBoard, winStatus
		);
		
		out.print(jsonResponse);
		out.flush();
	}
}