package ose.bionix.pe.tictactoe;

public class Game {
	private final Board board;
	private final Player player1;
	private final Player player2;
	private final UI ui;

	public Game(Board board, UI ui, Player player1, Player player2) {
		this.board = board;
		this.ui = ui;
		this.player1 = player1;
		this.player2 = player2;
	}

	/**
	 * Start the game loop. If firstIsPlayer2 is true, player2 moves first.
	 */
	public void run(boolean firstIsPlayer2) {
		Player current = firstIsPlayer2 ? player2 : player1;
		ui.print("Hello!");
		ui.displayBoard(board);
		ui.print(current == player1 ? "Player#1's turn" : "Player#2's turn");

		while (true) {
			int pos = current.chooseMove(board);
			if (pos == Player.QUIT) {
				break;
			}

			if (!board.setMove(pos, current.getMark())) {
				ui.print("The cell is occupied!");
				if (current == player1) {
					ui.print("Player#1's turn");
				}
				continue;
			}

			ui.displayBoard(board);
			int winner = board.checkWin();
			if (winner != Board.EMPTY) {
				ui.print(winner == player1.getMark() ? "Player#1 won!" : "Player#2 won!");
				break;
			}

			if (board.isBoardFull()) {
				ui.print("It is a draw!");
				break;
			}

			current = (current == player1) ? player2 : player1;
			ui.print(current == player1 ? "Player#1's turn" : "Player#2's turn");
		}

		ui.close();
	}
}
