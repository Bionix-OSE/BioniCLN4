package ose.bionix.pe.tictactoe;

public abstract class Player {
	public enum Type { HUMAN, COMPUTER }

	private final int mark; // Board.X or Board.O
	private final Type type;

	protected Player(int mark, Type type) {
		this.mark = mark;
		this.type = type;
	}

	public int getMark() { return mark; }
	public Type getType() { return type; }

	/**
	 * Decide a move on the provided board. Returns 0-based position or -1.
	 */
	public abstract int chooseMove(Board board);
}
