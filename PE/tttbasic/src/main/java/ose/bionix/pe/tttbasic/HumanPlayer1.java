package ose.bionix.pe.tttbasic;

import java.io.PrintStream;
import java.util.Scanner;

public class HumanPlayer1 extends Player {
	private final Scanner input;
	private final PrintStream output;

	public HumanPlayer1(int mark, Scanner input) {
		this(mark, input, System.out);
	}
	public HumanPlayer1(int mark, Scanner input, PrintStream output) {
		super(mark);
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean chooseMove(Board board) {
		while (true) {
			String line = null;
			output.print("Input a position [1-9]: ");
			try {
				line = input.nextLine().trim();
			} catch (RuntimeException ex) {
				output.println("There was an error handling user input.");
			}
			if ("q".equals(line)) {
				output.println("Game over.");
				return false;
			}

			int choice;
			try {
				choice = Integer.parseInt(line);
			} catch (NumberFormatException ex) {
				output.println("Please input a valid number [1-9]");
				continue;
			}

			int pos = choice - 1;
			if (board.getCell(pos) == Board.INVALID) {
				output.println("Please input a valid position [1-9]");
				continue;
			}
			if (board.isCellOccupied(pos)) {
				output.println("The cell is occupied!");
				continue;
			}

			board.place(pos, mark);
			return true;
		}
	}
}
