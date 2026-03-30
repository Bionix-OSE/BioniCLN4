package ose.bionix.pe.tictactoe;

public class Main {
	public static void main(String[] args) {
		boolean secondPlayerStarts = false;
		if (args.length > 0 && "2".equals(args[0])) secondPlayerStarts = true;

		UI ui = new ConsoleUI();
		Board board = new Board();
		Player p1 = new Player(Board.X, new InteractiveMoveStrategy(ui));
		Player p2 = new Player(Board.O, new FirstAvailableStrategy());

		Game game = new Game(board, ui, p1, p2);
		game.run(secondPlayerStarts);
	}
}
