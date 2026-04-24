package ose.bionix.pe.tictactoe;

public abstract class Player {
	public static final int QUIT = -1;

	private final int mark; // 1 / 2

	public Player(int mark) {
		this.mark = mark;
	}

	public int getMark() { return mark; }
	public abstract int chooseMove(Board board);
}
	