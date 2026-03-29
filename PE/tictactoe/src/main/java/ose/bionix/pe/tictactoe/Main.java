package ose.bionix.pe.tictactoe;

public class Main {
	public static void main(String[] args) {
		boolean computerStarts = false;
		if (args.length > 0 && "2".equals(args[0])) computerStarts = true;

		IO io = new ConsoleIO();
		Board board = new Board();
		Player human = new HumanPlayer(Board.X);
		Player computer = new ComputerPlayer(Board.O);

		Game game = new Game(board, io, human, computer);
		game.run(computerStarts);
	}
}
