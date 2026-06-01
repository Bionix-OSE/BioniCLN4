package ose.bionix.pe.tttbasic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		PrintStream output = System.out;

		if (args.length != 1 || (!"1".equals(args[0]) && !"2".equals(args[0]))) {
			System.out.println("Please input a valid first player [1-2]");
			return;
		}
		int firstPlayer = Integer.parseInt(args[0]);

		// Infrastructue for test units that we surely are gonna write at some point
		ByteArrayOutputStream captureBuffer = new ByteArrayOutputStream();
		PrintStream captureStream = new PrintStream(captureBuffer);
		captureStream.flush();

		Board board = new Board(output);
		Scanner input = new Scanner(System.in);
		HumanPlayer p1 = new HumanPlayer(Board.X, input, output);
		CPUPlayer p2 = new CPUPlayer(Board.O);

		Game game = new Game(board, p1, p2, output);
		game.run(firstPlayer);

		input.close();
		captureStream.close();
	}
}
