package ose.bionix.pe.tictactoe;

public class ComputerPlayer extends Player {
	public ComputerPlayer(int mark) { super(mark, Type.COMPUTER); }

	@Override
	public int chooseMove(Board board) {
		// Simple deterministic strategy: pick first available cell.
		return board.firstAvailableCell();
	}
}
