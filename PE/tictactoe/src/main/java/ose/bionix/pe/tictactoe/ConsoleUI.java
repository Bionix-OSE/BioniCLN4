package ose.bionix.pe.tictactoe;

public class ConsoleUI implements UI {
	private final java.util.Scanner scanner;

	public ConsoleUI() {
		scanner = new java.util.Scanner(System.in);
	}

	@Override
	public void print(String s) { System.out.print(s); }

	@Override
	public void println(String s) { System.out.println(s); }

	@Override
	public void println() { System.out.println(); }

	@Override
	public int promptCell() {
		if (!scanner.hasNextInt()) {
			scanner.nextLine();
			return -1;
		}
		int pick = scanner.nextInt();
		return pick - 1;
	}

	@Override
	public void close() {
		try { scanner.close(); } catch (Exception e) { }
	}

	@Override
	public void displayBoard(Board board) {
		println(toDisplayString(board));
	}

	public String toDisplayString(Board board) {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < 3; r++) {
			sb.append("+---+---+---+\n");
			sb.append("|");
			for (int c = 0; c < 3; c++) {
				int idx = r*3 + c;
				int v = board.getCell(idx);
				String ch;
				if (v == Board.EMPTY) ch = "-";
				else if (v == Board.X) ch = "X";
				else ch = "O";
				sb.append(' ').append(ch).append(' ').append('|');
			}
			sb.append('\n');
		}
		sb.append("+---+---+---+");
		return sb.toString();
	}
	
}
