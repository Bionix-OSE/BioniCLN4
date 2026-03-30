package ose.bionix.pe.tictactoe;

public class Board {
	public static final int EMPTY = -1;
	public static final int X = 1; // Player (human)
	public static final int O = 0; // Computer

	private int[] cells;

	public Board() {
		cells = new int[9];
		for (int i = 0; i < 9; i++) {
			cells[i] = EMPTY;
		}
	}

	public boolean makeMove(int pos, int mark) {
		if (pos < 0 || pos >= 9) return false;
		if (cells[pos] != EMPTY) return false;
		cells[pos] = mark;
		return true;
	}

	public int getCell(int pos) {
		if (pos < 0 || pos >= 9) return EMPTY;
		return cells[pos];
	}

	public boolean isFull() {
		for (int i = 0; i < 9; i++) {
			if (cells[i] == EMPTY) return false;
		}
		return true;
	}

	public int checkWin() {
		int[][] lines = {
				{0,1,2}, {3,4,5}, {6,7,8},
				{0,3,6}, {1,4,7}, {2,5,8},
				{0,4,8}, {2,4,6}
		};
		for (int[] l : lines) {
			int a = cells[l[0]], b = cells[l[1]], c = cells[l[2]];
			if (a != EMPTY && a == b && b == c) return a; // returns X (1) or O (0)
		}
		return EMPTY; // no winner yet
	}

	public int firstAvailableCell() {
		for (int i = 0; i < 9; i++) if (cells[i] == EMPTY) return i;
		return -1;
	}

	public String toDisplayString() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < 3; r++) {
			sb.append("+---+---+---+\n");
			sb.append("|");
			for (int c = 0; c < 3; c++) {
				int idx = r*3 + c;
				int v = cells[idx];
				String ch;
				if (v == EMPTY) ch = "-";
				else if (v == X) ch = "X";
				else ch = "O";
				sb.append(' ').append(ch).append(' ').append('|');
			}
			sb.append('\n');
		}
		sb.append("+---+---+---+");
		return sb.toString();
	}
}
