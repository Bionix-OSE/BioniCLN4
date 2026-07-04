// This is effectively the "client" code. It gets downloaded to the browser and also stores the game state.

document.addEventListener("DOMContentLoaded", () => {
	const statusDisplay = document.getElementById("status-display");
	const cells = document.querySelectorAll(".cell");
	const resetBtn = document.getElementById("reset-btn");
	
	let gameActive = true;
	let boardStrActive = "000000000"; // Game (board) state

	// Deserialize & render board string from the server
	function renderBoard(boardStr) {
		boardStrActive = boardStr; // We also update the board string in memory
		for (let i = 0; i < 9; i++) {
			const cellValue = boardStr.charAt(i);
			if (cellValue === "1") {
				cells[i].textContent = "X";
				cells[i].classList.add("occupied");
			} else if (cellValue === "2") {
				cells[i].textContent = "O";
				cells[i].classList.add("occupied");
			} else {
				cells[i].textContent = "";
				cells[i].classList.remove("occupied");
			}
		}
	}
	// This handles the text that displays above the board
	function updateStatus(status, winner) {
		if (status === "PLY") {
			statusDisplay.textContent = "You're X, computer is O";
			gameActive = true;
		} else {
			gameActive = false;
			if (winner === 1) statusDisplay.textContent = "You Win!";
			else if (winner === 2) statusDisplay.textContent = "CPU Wins!";
			else if (winner === 3) statusDisplay.textContent = "It's a Draw!";
		}
	}

	// Request handler wrapper function
	async function sendRequest(url, options = {}) {
		try {
			const res = await fetch(url, options);
			if (!res.ok) throw new Error(`Server returned ${res.status}`);
			const data = await res.json();
			renderBoard(data.board);
			updateStatus(data.status, data.winner);
		} catch (err) {
			console.error("Connection error:", err);
		}
	}
	// This handles POSTing to the server after every player move (cell click)
	function cellClickHandler(e) {
		const index = e.target.getAttribute("data-index");
		// Prevent action if game is completed or slot is already used
		if (!gameActive || e.target.classList.contains("occupied")) return;

		// Build the request body containing BOTH the move index and the current state string
		const params = new URLSearchParams();
		params.append("boardStr", boardStrActive);
		params.append("humanMove", index);
		// ...and send it back to the server
		sendRequest("./game", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: params
		});
	}

	// This one is self-explainatory
	function newGame() {
		sendRequest("./game");
	}

	// Attach the functions above to buttons on the page as events
	cells.forEach(cell => cell.addEventListener("click", cellClickHandler));
	resetBtn.addEventListener("click", newGame);

	newGame();
});
