package ose.bionix.pe.tttnai;

public class CPUPlayer extends Player {
	public CPUPlayer(int mark) {
		super(mark);
	}

	@Override
	public boolean chooseMove(Board board) {
		int targetCell = board.getFirstFreeCell();
		board.place(targetCell, mark);
		return true;
	}

	/**
	 * Returns the next move without modifying the board
	 */
	public int getMove(Board board) {
		return board.getFirstFreeCell();
	}
}
