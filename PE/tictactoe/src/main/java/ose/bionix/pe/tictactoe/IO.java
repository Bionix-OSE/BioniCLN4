package ose.bionix.pe.tictactoe;

public interface IO {
	void print(String s);
	void println(String s);
	void println();
	/**
	 * Prompt the human for a cell (1-9). Returns 0-based index or -1 if invalid.
	 */
	int promptCell();
	void close();
}
