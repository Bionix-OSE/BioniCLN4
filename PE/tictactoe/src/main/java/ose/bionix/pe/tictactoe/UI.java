package ose.bionix.pe.tictactoe;

import java.io.PrintStream;

public class UI {
	private final PrintStream printer;

	public UI(PrintStream out) {
		this.printer = out;
	}

	public void print(String s) { printer.println(s); }

	public void close() { }

	public void displayBoard(Board board) {
		print(toDisplayString(board));
	}

	private String toDisplayString(Board board) {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < 3; r++) {
			sb.append("| ");
			for (int c = 0; c < 3; c++) {
				int idx = r * 3 + c;
				int v = board.getCell(idx);
				sb.append(v);
				if (c < 2) sb.append(" | ");
			}
			sb.append(" |");
			if (r < 2) sb.append('\n');
		}
		return sb.toString();
	}
}
