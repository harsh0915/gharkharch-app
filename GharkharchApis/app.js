import express from 'express'
import https from 'https'
import fs from 'fs'
import {getCategories, getSubCategories,	getExpenses} from './database.js'

const app = express()

const options = {
	key: fs.readFileSync('./openssl/private.key'),      // Your private private-key
	cert: fs.readFileSync('./openssl/certificate.crt')      // Your SSL/TLS certificate
};

//pass the express app to manage queries made to
//the server
const server = https.createServer(options,app);

//listen at 443
server.listen(443, () => {
	console.log("HTTPS serever listening at port 443")
})

app.get('/', async (req, res) => {
	res.send("hello")
})

app.get('/sub_categories', async (req, res) => {
	const result = await getSubCategories()
	res.send(result) 
})
