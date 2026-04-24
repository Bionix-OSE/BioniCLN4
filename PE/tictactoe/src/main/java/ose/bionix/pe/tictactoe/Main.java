package ose.bionix.pe.tictactoe;

import java.io.PrintStream;

public class Main {
	public static void main(String[] args) {
		if (args.length != 1 || (!"1".equals(args[0]) && !"2".equals(args[0]))) {
			System.out.println("Please, input a valid option [1-2]");
			return;
		}

		boolean secondPlayerStarts = "2".equals(args[0]);
		UI ui = new UI(System.out);
		Board board = new Board();
		Player p1 = new HumanPlayer(Board.X, ui, new java.util.Scanner(System.in));
		Player p2 = new CPUPlayer(Board.O);

		Game game = new Game(board, ui, p1, p2);
		game.run(secondPlayerStarts);
	}
}
