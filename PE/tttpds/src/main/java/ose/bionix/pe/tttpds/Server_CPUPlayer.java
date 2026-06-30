package ose.bionix.pe.tttpds;

public class Server_CPUPlayer extends Player {
	public Server_CPUPlayer(int mark) {
		super(mark);
	}

	@Override
	public boolean chooseMove(Board board) {
		int targetCell = board.getFirstFreeCell();
		board.place(targetCell, mark);
		return true;
	}
}
