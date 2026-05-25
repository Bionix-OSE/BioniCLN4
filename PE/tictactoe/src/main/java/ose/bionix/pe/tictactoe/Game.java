package ose.bionix.pe.tictactoe;

import java.io.PrintStream;

public class Game {
	private final Board board;
	private final Player player1;
	private final Player player2;
	private final PrintStream output;

	public Game(Board board, Player player1, Player player2) {
		this(board, player1, player2, System.out);
	}

	public Game(Board board, Player player1, Player player2, PrintStream output) {
		this.board = board;
		this.player1 = player1;
		this.player2 = player2;
		this.output = output;
	}

	public void run(int firstPlayer) {
		Player[] playorder = {player1, player2};
		int current = firstPlayer;
		output.println("Hello!");
		board.display();

		while (true) {
			Player activePlayer = playorder[current - 1];
			output.println("Player" + activePlayer.getMark() + "'s turn");

			boolean game = activePlayer.chooseMove(board);
			if (!game) {
				return;
			}
			board.display();

			int winner = board.checkWin();
			if (winner != Board.EMPTY) {
				output.println("Player " + activePlayer.getMark() + " won!");
				break;
			}
			if (board.isBoardFull()) {
				output.print("It is a draw!");
				break;
			}

			current = 3 - current;
		}
	}
}
