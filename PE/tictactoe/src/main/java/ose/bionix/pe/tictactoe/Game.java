package ose.bionix.pe.tictactoe;

public class Game {
	private final Board board;
	private final Player player1;
	private final Player player2;
	private final UI ui;

	public Game() {
		UI console = new ConsoleUI();
		this.board = new Board();
		this.ui = console;
		this.player1 = new Player(Board.X, new InteractiveMoveStrategy(console));
		this.player2 = new Player(Board.O, new FirstAvailableStrategy());
	}

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

		ui.println("Tic-Tac-Toe");

		while (true) {
			int pos = current.chooseMove(board);

			if (pos < 0 || pos > 8) {
				ui.println("Please enter a number between 1 and 9.");
				continue;
			}

			if (!board.makeMove(pos, current.getMark())) {
				ui.println("Cell already taken. Try another.");
				continue;
			}

			int winner = board.checkWin();
			if (winner != Board.EMPTY) {
				ui.displayBoard(board);
				ui.println(winner == player1.getMark() ? "Player 1 wins!" : "Player 2 wins!");
				break;
			}

			if (board.isFull()) {
				ui.displayBoard(board);
				ui.println("It's a draw.");
				break;
			}

			current = (current == player1) ? player2 : player1;
		}

		ui.close();
	}
}
