package ose.bionix.pe.tttbasic;

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
}
