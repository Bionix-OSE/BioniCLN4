package ose.bionix.pe.tttnet;

public class HumanPlayer2 extends Player {
	private final GameServer_STMC.ClientSession session;

	public HumanPlayer2(int mark, GameServer_STMC.ClientSession session) {
		super(mark);
		this.session = session;
	}

	@Override
	public boolean chooseMove(Board board) {
		int move = session.takePendingMove();
		if (move < 0) {
			session.send("No move received. Please try again.\n");
			return false;
		}
		if (move < 1 || move > 9) {
			session.send("ERROR: Please enter a valid position [1-9]\n");
			return false;
		}
		int index = move - 1;
		if (board.isCellOccupied(index)) {
			session.send("ERROR: Cell is already occupied!\n");
			return false;
		}
		board.place(index, mark);
		return true;
	}
}
