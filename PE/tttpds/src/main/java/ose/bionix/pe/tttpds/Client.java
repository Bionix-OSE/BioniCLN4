package ose.bionix.pe.tttpds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private final String host;
	private final int port;
	private final Board board;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
		this.board = new Board();
	}

	private int getCell(int idx) {
		String boardserialized = board.serialize();
		return Character.getNumericValue(boardserialized.charAt(idx));
	}
	public void display(PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < 3; r++) {
			sb.append("| ");
			for (int c = 0; c < 3; c++) {
				int idx = r * 3 + c;
				String v = switch (getCell(idx)) {
					default -> "-";
					case Board.X -> "X";
					case Board.O -> "O";
				};
				sb.append(v);
				if (c < 2) sb.append(" | ");
			}
			sb.append(" |");
			if (r < 2) sb.append('\n');
		}
		out.println(sb.toString());
	}

	public void play() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(System.out, true)) {
			
		} catch (Exception e) {}
	}
}
