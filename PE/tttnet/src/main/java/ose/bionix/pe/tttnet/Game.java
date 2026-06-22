package ose.bionix.pe.tttnet;

import java.io.PrintWriter;

public class Game implements Runnable {
	private final Board board;
	private final Player player1;
	private final Player player2;
	private final PrintWriter output;
	public int firstPlayer;

	public Game(Board board, Player player1, Player player2, PrintWriter out) {
		this.board = board;
		this.player1 = player1;
		this.player2 = player2;
		this.output = out;
	}

	@Override
	public void run() {
		Player[] playorder = {player1, player2};
		int current = firstPlayer;
		output.println("INFO: Hello!");
		board.display();

		while (true) {
			Player activePlayer = playorder[current - 1];
			output.println("INFO: Player" + activePlayer.getMark() + "'s turn");

			boolean game = activePlayer.chooseMove(board);
			if (!game) {
				return;
			}
			board.display();

			int winner = board.checkWin();
			if (winner != Board.EMPTY) {
				output.println("INFO: Player " + activePlayer.getMark() + " won!");
				break;
			}
			if (board.isBoardFull()) {
				output.print("INFO: It is a draw!");
				break;
			}

			current = 3 - current;
		}
	}
}
