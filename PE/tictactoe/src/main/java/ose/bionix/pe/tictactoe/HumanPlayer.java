package ose.bionix.pe.tictactoe;

import java.util.Scanner;

public class HumanPlayer extends Player {
	private final UI ui;
	private final Scanner input;

	public HumanPlayer(int mark, UI ui, Scanner input) {
		super(mark);
		this.ui = ui;
		this.input = input;
	}

	@Override
	public int chooseMove(Board board) {
		while (true) {
			String line = input.nextLine().trim();
			if ("q".equals(line)) {
				ui.print("End of the game");
				return QUIT;
			}

			int choice;
			try {
				choice = Integer.parseInt(line);
			} catch (NumberFormatException ex) {
				ui.print("Please, input a valid number [1-9]");
				ui.print("Player#1's turn");
				continue;
			}

			if (choice < 1 || choice > 9) {
				ui.print("Please, input a valid number [1-9]");
				ui.print("Player#1's turn");
				continue;
			}

			int pos = choice - 1;
			if (board.isCellOccupied(pos)) {
				ui.print("The cell is occupied!");
				ui.print("Player#1's turn");
				continue;
			}

			return pos;
		}
	}
}
