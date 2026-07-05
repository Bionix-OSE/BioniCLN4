// Player.java: Player object for both player types
// This class is the exact same across the two versions, except for the package name.

package ose.bionix.pe.ttthttp;

public abstract class Player {
	public static final int QUIT = -1;
	public final int mark; // 1 / 2

	public Player(int mark) {
		this.mark = mark;
	}

	public int getMark() { return mark; }
	public abstract boolean chooseMove(Board board);
}
