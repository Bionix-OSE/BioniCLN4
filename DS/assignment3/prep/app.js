const express = require('express');
const app = express();
app.use(express.json());

let userlist = [
	{ id: 1, name: 'Azuna', email: 'azuna@kleerefind.cc' },
	{ id: 2, name: 'Bionic', email: 'bionic@osenet.work' }
];
const MSG_USER_404 = 'User not found or invalid'

function userlookup(id, res) {
	const user = userlist.find(u => u.id == id); // Lookup user in userlist by id, where u.id equal to id
	if (!user) {res.status(404).json({message: MSG_USER_404}); return NaN};
	return user
}

// GET: All users
app.get('/users', (req, res) => {
	// Where:
	// - req - Request object: Contains incoming request from client
	// - res - Response object: Use this to talk back to the client
	res.json(userlist) // Return a JSON containing users to the client in this case
});

// GET: A specific user
// How to on client: curl <-X GET> lo:80/users/1 (1 is the id here)
app.get('/users/:id', (req, res) => {
	let user = userlookup(parseInt(req.params.id), res)
	res.json(user) // Same return
});

// PUT: Modify a user
// How to on client: curl -X PUT -H "Content-Type: application/json" -d '{"name":"NewName";"email";"new@ema.il"}' lo:80/users/1
// Can also read from local JSON: curl -X PUT -H "Content-Type: application/json" -d "@clientdata.json" lo:80/users/1
app.put('/users/:id', (req, res) => {
	let user = userlookup(parseInt(req.params.id), res)
	// We modify the user before returning it
	user.name = req.body.name
	user.email = req.body.email
	res.json(user)
});

// POST: Add a user
// How to on client: Use the same commands as PUT but change -X to POST, and add id to request JSON
app.post('/users/add', (req, res) => {
	let newid = parseInt(req.body.id)
	if (userlist.some(u => u.id == newid)) {return res.status(400).json({message: "User ID already exists"})}
	let user = {}
	user.id = newid
	user.name = req.body.name
	user.email = req.body.email
	userlist.push(user)
	res.json(user)
});

// DELETE: A specific user
// How to on client: curl -X DELETE lo:80/users/1
app.delete('/users/:id', (req, res) => {
	let user = userlookup(parseInt(req.params.id), res)
	const userdel = userlist.splice(user, 1)
	res.json(userdel[0])
});

// Main app
const port = 80
app.listen(port, () => {
	console.log(`Server is up on port ${port}`)
});
