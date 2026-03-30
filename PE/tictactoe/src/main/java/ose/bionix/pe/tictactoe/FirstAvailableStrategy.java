package ose.bionix.pe.tictactoe;

public class FirstAvailableStrategy implements MoveStrategy {
	@Override
	public int chooseMove(Board board) {
		return board.firstAvailableCell();
	}
}
