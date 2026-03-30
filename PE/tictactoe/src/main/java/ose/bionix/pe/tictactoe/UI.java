package ose.bionix.pe.tictactoe;

public interface UI {
	void print(String s);
	void println(String s);
	void println();
	int promptCell();
	void close();
	void displayBoard(Board board);
}
