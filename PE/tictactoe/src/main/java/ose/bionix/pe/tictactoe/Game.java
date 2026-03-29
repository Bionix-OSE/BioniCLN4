package ose.bionix.pe.tictactoe;

public class Game {
	private final Board board;
	private final Player human;
	private final Player computer;
	private final IO io;

	public Game() {
		this(new Board(), new ConsoleIO(), new HumanPlayer(Board.X), new ComputerPlayer(Board.O));
	}

	public Game(Board board, IO io, Player human, Player computer) {
		this.board = board;
		this.io = io;
		this.human = human;
		this.computer = computer;
	}

	/**
	 * Start the game loop. If firstIsComputer is true, computer moves first.
	 */
	public void run(boolean firstIsComputer) {
		Player current = firstIsComputer ? computer : human;

		io.println("Tic-Tac-Toe (CLI)");
		io.println("Human is X. Computer is O.\n");

		while (true) {
			if (current.getType() == Player.Type.HUMAN) {
				io.println(board.toDisplayString());
				io.print("Enter cell (1-9): ");
				int pos = io.promptCell();
				if (pos < 0 || pos > 8) {
					io.println("Please enter a number between 1 and 9.");
					continue;
				}
				if (!board.makeMove(pos, human.getMark())) {
					io.println("Cell already taken. Try another.");
					continue;
				}
			} else {
				int pos = computer.chooseMove(board);
				if (pos >= 0) {
					board.makeMove(pos, computer.getMark());
					io.println("Computer chooses: " + (pos + 1));
				}
			}

			int winner = board.checkWin();
			if (winner != Board.EMPTY) {
				io.println(board.toDisplayString());
				if (winner == human.getMark()) io.println("Human (X) wins!");
				else io.println("Computer (O) wins!");
				break;
			}

			if (board.isFull()) {
				io.println(board.toDisplayString());
				io.println("It's a draw.");
				break;
			}

			current = (current == human) ? computer : human;
		}

		io.close();
	}
}
