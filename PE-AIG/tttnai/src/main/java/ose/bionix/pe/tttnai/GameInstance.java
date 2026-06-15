package ose.bionix.pe.tttnai;

import java.io.*;
import java.util.concurrent.*;

/**
 * Manages a single game between a human client (Player 1, X) and CPU player (Player 2, O).
 * Each client connection gets its own GameInstance running in a separate thread.
 * 
 * Communication protocol:
 * - Server → Client: GAME_START:message
 * - Server → Client: BOARD:state|TURN:playerNum
 * - Server → Client: ERROR:message
 * - Server → Client: END:message
 * - Client → Server: MOVE:position (0-8)
 * - Client → Server: QUIT
 */
public class GameInstance implements Runnable {
	private Board board;
	private CPUPlayer cpuPlayer;
	private PrintWriter out;
	private BlockingQueue<Integer> moveQueue;
	private volatile boolean gameActive;
	private int currentPlayer; // 1 = human (X), 2 = CPU (O)

	public GameInstance(PrintWriter out) {
		this.out = out;
		this.board = new Board();
		this.cpuPlayer = new CPUPlayer(Board.O);
		this.moveQueue = new LinkedBlockingQueue<>();
		this.gameActive = true;
		this.currentPlayer = 1; // Human always starts
	}

	/**
	 * Main game loop - runs in separate thread managed by ClientHandler
	 */
	@Override
	public void run() {
		try {
			sendMessage("GAME_START:You are Player 1 (X), Computer is Player 2 (O)");
			broadcastBoardState();

			while (gameActive) {
				if (currentPlayer == 1) {
					// Human's turn - wait for move with timeout
					Integer position = moveQueue.poll(60, TimeUnit.SECONDS);
					if (position == null) {
						sendMessage("END:Game timeout - no move received within 60 seconds");
						gameActive = false;
						break;
					}

					// Validate move
					if (position < 0 || position > 8) {
						sendMessage("ERROR:Invalid position (must be 0-8)");
						continue;
					}
					if (board.isCellOccupied(position)) {
						sendMessage("ERROR:Position already occupied");
						continue;
					}

					// Place human's move
					board.place(position, Board.X);
					System.out.println("[GameInstance] Human played at position " + position);
					broadcastBoardState();

					// Check win conditions
					if (checkGameEnd(Board.X)) {
						break;
					}

					// Switch to CPU's turn
					currentPlayer = 2;
					broadcastBoardState();

				} else {
					// CPU's turn
					int cpuMove = cpuPlayer.getMove(board);
					if (cpuMove == -1) {
						// No moves available (shouldn't happen, board full)
						sendMessage("END:It's a draw!");
						gameActive = false;
						break;
					}

					board.place(cpuMove, Board.O);
					System.out.println("[GameInstance] CPU played at position " + cpuMove);
					broadcastBoardState();

					// Check win conditions
					if (checkGameEnd(Board.O)) {
						break;
					}

					// Switch back to human's turn
					currentPlayer = 1;
					broadcastBoardState();
				}
			}
		} catch (InterruptedException e) {
			System.out.println("[GameInstance] Game interrupted");
			Thread.currentThread().interrupt();
		} finally {
			gameActive = false;
			out.flush();
		}
	}

	/**
	 * Check for win or draw, and send appropriate end message
	 * @return true if game ended, false otherwise
	 */
	private boolean checkGameEnd(int player) {
		int winner = board.checkWin();
		if (winner == Board.X) {
			sendMessage("END:You won!");
			gameActive = false;
			return true;
		} else if (winner == Board.O) {
			sendMessage("END:Computer won!");
			gameActive = false;
			return true;
		}

		if (board.isBoardFull()) {
			sendMessage("END:It's a draw!");
			gameActive = false;
			return true;
		}

		return false;
	}

	/**
	 * Queue a move submitted by the client
	 */
	public void submitMove(int position) {
		if (!gameActive) {
			System.out.println("[GameInstance] Game not active - move ignored");
			return;
		}
		if (currentPlayer != 1) {
			sendMessage("ERROR:Not your turn");
			return;
		}
		if (position < 0 || position > 8) {
			sendMessage("ERROR:Invalid position (must be 0-8)");
			return;
		}
		try {
			boolean queued = moveQueue.offer(position, 5, TimeUnit.SECONDS);
			if (!queued) {
				sendMessage("ERROR:Move queue full");
			}
		} catch (InterruptedException e) {
			System.out.println("[GameInstance] Interrupted while queuing move");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Broadcast current board state to client
	 */
	private void broadcastBoardState() {
		String boardState = boardToString();
		sendMessage("BOARD:" + boardState + "|TURN:" + currentPlayer);
	}

	/**
	 * Convert board to string format for transmission
	 */
	private String boardToString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			int cell = board.getCell(i);
			if (cell == Board.X) {
				sb.append("X");
			} else if (cell == Board.O) {
				sb.append("O");
			} else {
				sb.append(" ");
			}
			if (i < 8) sb.append(",");
		}
		return sb.toString();
	}

	/**
	 * Send message to client with synchronization
	 */
	private void sendMessage(String message) {
		synchronized (out) {
			out.println(message);
			out.flush();
		}
	}

	public boolean isActive() {
		return gameActive;
	}

	/**
	 * Force game to end (e.g., on client disconnect)
	 */
	public void end() {
		gameActive = false;
	}
}
