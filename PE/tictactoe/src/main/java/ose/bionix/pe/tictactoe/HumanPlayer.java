package ose.bionix.pe.tictactoe;

public class HumanPlayer extends Player {
    public HumanPlayer(int mark) { super(mark, Type.HUMAN); }

    @Override
    public int chooseMove(Board board) {
        // Human input handled by Game via IO.promptCell(), so return -1 when called.
        return -1;
    }
}
