const socket = io(); // Connect to the server
const chatbox = document.getElementById("chatbox");

chatbox.addEventListener('keyup', (e) => {
	e.preventDefault(); // Prevent form submission
	if (e.key === 'Enter' && chatbox.value) {
		sendMessage();
	}
});
		
function sendMessage() {
	const username = document.getElementById("username-field").value;
	const msg = chatbox.value;
	// Send the message to the server
	socket.emit('message', `${username}: ${msg}`);
	chatbox.value = ''; // Clear the input field
}

// Listen for messages from the server
socket.on('message', (msgData) => {
	const messageElement = document.createElement('div');
	messageElement.textContent = msgData;
	document.getElementById("messages").appendChild(messageElement);
});