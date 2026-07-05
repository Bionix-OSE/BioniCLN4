// Server_CPUPlayer.java
// This class is the exact same across the two versions, except for the package name.

package ose.bionix.pe.ttthttp;

public class Server_CPUPlayer extends Player {
	public Server_CPUPlayer(int mark) {
		super(mark);
	}

	@Override
	public boolean chooseMove(Board board) {
		// It simply chooses the next free cell from 1-9 and place its move there
		int targetCell = board.getFirstFreeCell();
		board.place(targetCell, mark);
		return true;
	}
}
