package ose.bionix.pe.tictactoe;

public class CPUPlayer extends Player {
	public CPUPlayer(int mark) {
		super(mark);
	}

	@Override
	public int chooseMove(Board board) {
		for (int i = 0; i < 9; i++) if (board.getCell(i) == -1) return i;
		return -1;
	}
}
