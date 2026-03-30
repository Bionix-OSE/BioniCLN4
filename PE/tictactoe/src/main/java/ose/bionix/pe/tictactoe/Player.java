package ose.bionix.pe.tictactoe;

public class Player {
	private final int mark; // Board.X or Board.O
	private final MoveStrategy strategy;

	public Player(int mark, MoveStrategy strategy) {
		this.mark = mark;
		this.strategy = strategy;
	}

	public int getMark() { return mark; }

	public int chooseMove(Board board) {
		return strategy.chooseMove(board);
	}
}
