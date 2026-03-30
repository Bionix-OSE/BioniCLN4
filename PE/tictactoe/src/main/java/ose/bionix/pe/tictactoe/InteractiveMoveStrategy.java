package ose.bionix.pe.tictactoe;

public class InteractiveMoveStrategy implements MoveStrategy {
	private final UI ui;

	public InteractiveMoveStrategy(UI ui) {
		this.ui = ui;
	}

	@Override
	public int chooseMove(Board board) {
		ui.displayBoard(board);
		ui.print("Enter cell (1-9): ");
		int pos = ui.promptCell();
		return pos;
	}
}
