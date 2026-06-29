package ose.bionix.pe.tttnet;

import java.io.PrintWriter;

public class Game implements Runnable {
	private final Board board;
	private final Player player1;
	private final Player player2;
	private final PrintWriter output;
	public int firstPlayer;
	private Player[] playorder;
	private int current;
	private boolean finished;

	public Game(Board board, Player player1, Player player2, PrintWriter out) {
		this.board = board;
		this.player1 = player1;
		this.player2 = player2;
		this.output = out;
	}

	private void initializeTurnOrder() {
		if (playorder == null) {
			playorder = new Player[] {player1, player2};
			current = firstPlayer;
		}
	}

	public boolean playNextTurn() {
		initializeTurnOrder();
		Player activePlayer = playorder[current - 1];
		output.println("INFO: Player" + activePlayer.getMark() + "'s turn");

		boolean game = activePlayer.chooseMove(board);
		if (!game) {
			finished = true;
			output.println("QUIT");
			return false;
		}
		board.display();

		int winner = board.checkWin();
		if (winner != Board.EMPTY) {
			output.println("INFO: Player " + activePlayer.getMark() + " won!");
			finished = true;
			output.println("QUIT");
			return false;
		}
		if (board.isBoardFull()) {
			output.println("INFO: It is a draw!");
			finished = true;
			output.println("QUIT");
			return false;
		}

		current = 3 - current;
		return true;
	}

	public boolean isFinished() {
		return finished;
	}

	@Override
	public void run() {
		output.println("INFO: Hello!");
		board.display();
		while (playNextTurn()) {}
	}
}
