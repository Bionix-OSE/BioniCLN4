// Board.java: Board object and the backbone of the game
// Also includes serialize and deserialize functions to aid with network communications.
// This class is the exact same across the two versions, except for the package name.

package ose.bionix.pe.ttthttp;

public class Board {
	public static final int EMPTY = 0;
	public static final int X = 1; // Player (human)
	public static final int O = 2; // Computer
	public static final int DRAW = 3;

	private final int[] cells;

	public Board() {
		cells = new int[9];
		for (int i = 0; i < 9; i++) {
			cells[i] = EMPTY;
		}
	}

	private int getCell(int pos) {
		if (pos < 0 || pos >= cells.length) return EMPTY;
		return cells[pos];
	}
	public int getFirstFreeCell() { // For CPU move
		for (int i = 0; i < cells.length; i++) {
			if (getCell(i) == EMPTY) {return i;}
		}
		return -1;
	}
	public boolean isCellOccupied(int pos) {
		return getCell(pos) != EMPTY;
	}
	public boolean place(int pos, int mark) {
		if (isCellOccupied(pos)) return false;
		cells[pos] = mark;
		return true;
	}

	private boolean isBoardFull() {
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] == EMPTY) return false;
		}
		return true;
	}
	public int checkWin() {
		int[][] lines = { // Win conditions:
			{0,1,2}, {3,4,5}, {6,7,8}, // 3 in any row
			{0,3,6}, {1,4,7}, {2,5,8}, // 3 in any column
			{0,4,8}, {2,4,6} // 3 in any diagonal
		};
		for (int[] l : lines) {
			int a = cells[l[0]], b = cells[l[1]], c = cells[l[2]];
			if (a != EMPTY && a == b && b == c) return a; // Returns X (1) or O (0)
		}
		if (isBoardFull()) return DRAW; // If no conditions match, but board full, must be a draw.
		return EMPTY; // no winner yet
	}

	public String serialize() {
		// Turns the board array into a numeric string and return the string
		// [1][0][2][1][2][0][1][0][0] -> "102120100"
		StringBuilder sb = new StringBuilder();
		for (int cell : cells) {
			sb.append(cell);
		}
		return sb.toString();
	}
	public void deserialize(String state) {
		// And this does the exact opposite, except it updates the object's cells with the conversion result directly.
		if (state == null || state.length() != 9) return;
		for (int i = 0; i < 9; i++) {
			cells[i] = Character.getNumericValue(state.charAt(i));
		}
	}
}
