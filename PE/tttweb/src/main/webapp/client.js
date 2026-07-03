document.addEventListener("DOMContentLoaded", () => {
	const statusDisplay = document.getElementById("status-display");
	const cells = document.querySelectorAll(".cell");
	const resetBtn = document.getElementById("reset-btn");
	
	let gameActive = true;

	// Synchronize frontend layout with backend matrix string definitions (e.g. "102000000")
	function renderBoard(boardString) {
		for (let i = 0; i < 9; i++) {
			const cellValue = boardString.charAt(i);
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

	// Handles text prompts depending on match resolution conditions
	function updateStatus(status, winner) {
		if (status === "PLY") {
			statusDisplay.textContent = "Your Turn (X)";
			gameActive = true;
		} else {
			gameActive = false;
			if (winner === 1) {
				statusDisplay.textContent = "🎉 Match Over: You Win!";
			} else if (winner === 2) {
				statusDisplay.textContent = "💻 Match Over: CPU Wins!";
			} else if (winner === 3) {
				statusDisplay.textContent = "🤝 Match Over: It's a Draw!";
			}
		}
	}

	// Initialize Game Setup Sequence
	function startNewGame() {
		fetch("game")
			.then(res => res.json())
			.then(data => {
				renderBoard(data.board);
				updateStatus(data.status, data.winner);
			})
			.catch(err => console.error("Initialization Failed:", err));
	}

	// Send click coordinates to Server API
	function handleCellClick(e) {
		const index = e.target.getAttribute("data-index");

		// Prevent action if game is completed or slot is already used
		if (!gameActive || e.target.classList.contains("occupied")) return;

		// Use URLSearchParams to structure default POST request parameters securely
		const params = new URLSearchParams();
		params.append("position", index);

		fetch("game", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: params
		})
		.then(res => res.json())
		.then(data => {
			renderBoard(data.board);
			updateStatus(data.status, data.winner);
		})
		.catch(err => console.error("Move update execution failed:", err));
	}

	// Assign event attachments
	cells.forEach(cell => cell.addEventListener("click", handleCellClick));
	resetBtn.addEventListener("click", startNewGame);

	// Boot execution loop 
	startNewGame();
});
