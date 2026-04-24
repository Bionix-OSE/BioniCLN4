// On Linux, with mongodb installed, run: mongod --dbpath ~/mongodb/data --logpath ~/mongodb.log (feel free to change paths around)
// Then run this with: mongosh db_setup.js

db = db.getSiblingDB('agriproducts');
db.products.insertOne({
	id: '8930001234567',
	name: 'Dragon Fruit',
	origin: 'Viet Nam',
	farm: 'La Pitaya Valley Cooperative',
	website: 'https://pitahayavalley.mx',
	energy: '36 kcal/100g',
	expiry: 'before 25/04/2027'
});
printjson(db.products.findOne({name: 'Dragon Fruit'}));
