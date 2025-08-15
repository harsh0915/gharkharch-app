import express from 'express'
import https from 'https'
import fs from 'fs'
import {getUser} from './database.js'
import dotenv from "dotenv"

dotenv.config();
const app = express()
const options = {
	key: fs.readFileSync('./openssl/private.key'),      	
	cert: fs.readFileSync('./openssl/certificate.crt')      
}

//pass the express app
const server = https.createServer(options,app);

server.listen(process.env.PORT, () => {
	console.log("HTTPS serever listening at port 443")
})

app.get('/', async (req, res) => {
	res.send("hello")
})

app.get('/sub_categories', async (req, res) => {
	const result = await getUser()
	res.send(result) 
})
