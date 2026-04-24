package ose.bionix.pe.tictactoe;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
	private Board board;

	@BeforeEach
	public void setUp() {
		board = new Board();
	}

	@Test
	public void testBoardInit() {
		// All cells should be empty initially
		for (int i = 0; i < 9; i++) {
			assertEquals(board.getCell(i), Board.EMPTY);
		}
	}
	@Test
	public void testCellValue() {
		assertEquals(board.getCell(0), Board.EMPTY);
	}
	@Test
	public void testCanMakeMove() {
		assertTrue(board.setMove(0, 1));
		assertTrue(board.isCellOccupied(0));
		assertEquals(board.getCell(0), 1);
	}
}
