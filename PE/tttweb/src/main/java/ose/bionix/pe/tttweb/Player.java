package ose.bionix.pe.tttweb;

public abstract class Player {
	public static final int QUIT = -1;
	public final int mark; // 1 / 2

	public Player(int mark) {
		this.mark = mark;
	}

	public int getMark() { return mark; }
	public abstract boolean chooseMove(Board board);
}
