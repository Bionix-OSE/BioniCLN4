package ose.bionix.pe.tttnet;

import java.io.PrintWriter;
import java.io.BufferedReader;

public class HumanPlayer extends Player {
	private final BufferedReader input;
	private final PrintWriter output;

	public HumanPlayer(int mark, BufferedReader input, PrintWriter output) {
		super(mark);
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean chooseMove(Board board) {
		try {
			output.print("Input a position [1-9]: ");
			String line = input.readLine();
			if (line == null || "q".equals(line)) return false;

			int pos;
			try {
				pos = Integer.parseInt(line.trim());
				if (pos < 1 || pos > 9) {throw new NumberFormatException();}
			} catch (Exception e) {
				output.println("ERROR: Please enter a valid position [1-9]");
				return chooseMove(board);
			}
			if (board.isCellOccupied(pos - 1)) {
				output.println("ERROR: Cell is already occupied!");
				return chooseMove(board);
			}

			board.place(pos - 1, mark);
			return true;
		} catch (Exception e) {return false;}
	}
}
