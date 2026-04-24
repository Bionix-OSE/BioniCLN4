// const class = require('module') = import class from 'module';
// Can do either
const express = require('express');
const http = require('http');
const path = require('path');
const { Server } = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = new Server(server);
const port = process.env.port || 1507;

app.use(express.static(path.join(__dirname, '../client/public'))); // Serve static files from the 'public' directory

io.on('connection', (socket) => {
	console.log('a user connected');

	socket.on('disconnect', () => {
		console.log('user disconnected');
	});

	socket.on('message', (msgData) => {
		console.log('message: ' + msgData);
		io.emit('message', msgData); // Broadcast the message to all connected clients
	});
});

server.listen(port, () => console.log(`Server running on port ${port}`));
