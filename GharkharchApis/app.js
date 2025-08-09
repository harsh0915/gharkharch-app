import express from 'express'
import https from 'https'
//import fs from 'fs'
import {getCategories, getSubCategories,	getExpenses} from './database.js'

//const server = https.createServer();
const app = express()

app.get('/', async (req, res) => {
	const result = await getSubCategories()
  res.send(result[0]) 
})

app.listen(3000,'0.0.0.0',()=> {
console.log("Server is running!")
}) 

//const options = {
//  key: fs.readFileSync('private-key.pem'),      // Your private private-key
//  cert: fs.readFileSync('certificate.pem')      // Your SSL/TLS certificate
//};

//server.listen(options, (req, res) => {
//	res.writeHead(200);
//}).listen(443, () => {
//	console.log("HTTPS serever listening at port 443")
//})
