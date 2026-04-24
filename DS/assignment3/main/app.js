const express = require('express')
const mongo = require('mongodb')
const fs = require('fs')
const path = require('path')
const app = express()
const MongoClient = mongo.MongoClient
const url = 'mongodb://localhost:27017/'

function senderr(res, status, err = null) {
	res.status(status)
	let html = fs.readFileSync(path.join(__dirname, `public/html/${status}.html`), 'utf8')
	if (err) {html = html.replace(/{{error}}/g, err.message)}
	res.send(html)
}

app.use(express.static(path.join(__dirname, 'public')))
let client
MongoClient.connect(url).then(connectedClient => {
	client = connectedClient
	console.log('Connected to MongoDB')
}).catch(err => {
	console.error('Connection error:', err)
	process.exit(1)
})

// Access with e.g.: http://site.url/product/8930001234567
app.get('/product/:id', async (req, res) => {
	if (!client) {senderr(res, 500); return}
	const dbo = client.db('agriproducts')
	const collection = dbo.collection('products')
	try {
		const result = await collection.findOne({ id: req.params.id })
		if (result) {
			let html = fs.readFileSync(path.join(__dirname, 'public/html/product.html'), 'utf8')
			html = html.replace(/{{id}}/g, result.id)
				.replace(/{{name}}/g, result.name)
				.replace(/{{origin}}/g, result.origin)
				.replace(/{{farm}}/g, result.farm)
				.replace(/{{website}}/g, result.website)
				.replace(/{{energy}}/g, result.energy)
				.replace(/{{expiry}}/g, result.expiry)
			res.send(html)
		} else {senderr(res, 404)}
	} catch (err) {senderr(res, 500, err)}
});

const port = 8080
app.listen(port, () => {
	console.log(`Server running on port ${port}`)
});
