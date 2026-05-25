package ose.bionix.pe.tictactoe;

import java.io.PrintStream;

public class Board {
	public static final int INVALID = -1;
	public static final int EMPTY = 0;
	public static final int X = 1; // Player (human)
	public static final int O = 2; // Computer

	private final int[] cells;
	private final PrintStream output;

	public Board() {
		this(System.out);
	}
	public Board(PrintStream output) {
		cells = new int[9];
		for (int i = 0; i < 9; i++) {
			cells[i] = EMPTY;
		}
		this.output = output;
	}

	public int getCell(int pos) {
		if (pos < 0 || pos >= cells.length) return EMPTY;
		return cells[pos];
	}
	public int getFirstFreeCell() {
		for (int i = 0; i < cells.length; i++) {
			if (getCell(i) == EMPTY) {return i;}
		}
		return -1;
	}
	public boolean isCellOccupied(int pos) {
		return getCell(pos) != EMPTY;
	}
	public boolean isBoardFull() {
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] == EMPTY) return false;
		}
		return true;
	}
	public boolean place(int pos, int mark) {
		if (isCellOccupied(pos)) return false;
		cells[pos] = mark;
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

	public void display() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < 3; r++) {
			sb.append("| ");
			for (int c = 0; c < 3; c++) {
				int idx = r * 3 + c;
				String v = switch (getCell(idx)) {
					default -> "-";
					case X -> "X";
					case O -> "O";
				};
				sb.append(v);
				if (c < 2) sb.append(" | ");
			}
			sb.append(" |");
			if (r < 2) sb.append('\n');
		}
		output.println(sb.toString());
	}
}
