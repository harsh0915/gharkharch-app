import express from 'express'
import https from 'https'
import fs from 'fs'
import bcrypt from "bcrypt"
import crypto from "crypto"
import jwt from "jsonwebtoken"
import dotenv from "dotenv"
import { initDB, getUserDetails, insertPassword, checkSubStatus, COLUMNS } from './database.js'
import { generateTokens, generateAccesToken } from './token_generator.js'

dotenv.config({debug: false});

const app = express()
app.use(express.json())
//read the certificate and key in the options object and provide it 
//to the https server, and use the express app for the api endpoint listener
const options = {
	key: fs.readFileSync('./openssl/private.key'),
	cert: fs.readFileSync('./openssl/certificate.crt')
}

//provide the express app and options obj
const server = https.createServer(options, app);

(async () => {
	try {
		//initilaize db then start listening for requests
		await initDB()

		server.listen(process.env.PORT, () => {
			console.log("Serever listening at port 443")
		})
	}
	catch (e) {
		console.log(`DB init failed ${e.message}`)
		process.exit(1);
	}
})()

app.post('/login', async (req, res) => {
	try {
		console.log(`Received a login req from ${req.body.name}`)
		const user_name = req.body.name
		const password = req.body.password
		const user = await getUserDetails(user_name)
		//no user with this user_name
		if (!user) return res.status(404).send("No such user found in the database")

		if (user[COLUMNS.IS_SUB_ACTIVE] == 0) return res.status(403).send("Subscription revoked")

		//remember to use transaction later
		//check if its the first login, if so then accept the newly entered password;onboarding logic
		if (user[COLUMNS.USER_PSSWRD] == null) {
			const salt = await bcrypt.genSalt()
			const hashed_pass = await bcrypt.hash(password, salt)
			const allgood = await insertPassword(hashed_pass, user_name)
			//check if the stateful token was inserted successfully
			if (allgood == 1) {
				const tokens = await generateTokens(user_name)
				return res.status(200).json(tokens)
			} else {
				return res.status(500).send("Auth Failed : Failed to generate tokens")
			}
		}
		else {
			//this would trigger when a user has to login again after getting his subscription revoked
			if (bcrypt.compareSync(password, user[COLUMNS.USER_PSSWRD])) {
				const tokens = await generateTokens(user_name)
				return res.status(200).json(tokens)
			} else {
				return res.status(403).send("Auth Failed : Wrong password")
			}
		}
	}
	catch (e) {
		res.status(500).send(`Oops something went wrong! Error : ${e.message}`)
	}
})

app.get('/verify', async (req, res) => {
	//if its a consecutive login use tokens to simulate SSO 
	const token = req.headers.authorization

	if (!token) return res.status(401).send({ access_granted: false, message: "Token Missing" })

	try {
		const decoded_token = jwt.verify(token, process.env.SECRET)
		//check if the user has an active subscription
		const isSubActive = await checkSubStatus(decoded_token.username)
		//check user's subscription status 
		if (isSubActive == 1) {
			res.status(200).json({ access_granted: true, message: "Access Granted" })
		} else {
			res.status(403).json({ access_granted: false, message: "Subscription Revoked" })
		}
	}
	catch (e) {
		if (e.name == "TokenExpiredError") {
			return res.status(403).send("Token Expired")
		} else {
			console.log("Received an invalid token!!!")
			return res.status(403).send(`Invalid Token ${e.message}`)
		}
	}
})

app.post('/refresh', async (req, res) => {
	try {
		console.log(`${req.body.name} is refreshing the tokens`)
		const user_name = req.body.name
		const user_token = req.body.refresh_token
		const user = await getUserDetails(user_name)
		//no user with this user_name
		if (!user) return res.status(404).send("No such user found in the database")
		//check if user has a valid subscription
		if (user[COLUMNS.IS_SUB_ACTIVE] == 0) return res.status(403).send("Subscription revoked")
		//check if refresh token is valid then generate new token otherwise return access denied
		const refresh_token = user[COLUMNS.REFRESH_TOKEN]
		//need to generate buffers to compare the hashed string from db and req
		const urf_buffer = Buffer.from(user_token, "hex")
		const rf_buffer = Buffer.from(refresh_token, "hex")
		//compare the refresh token buffers
		if (crypto.timingSafeEqual(rf_buffer, urf_buffer)) {
			//Date.now() gives current time in milliss
			const now = Date.now()
			//convert db timestamp to millis; epoch time
			const expiry = new Date(user[COLUMNS.EXPIRY]).getTime()
			//refresh token expired; renew the token 
			if (now > expiry) {
				const tokens = await generateTokens(user_name)
				return res.status(200).json(tokens)
			} else {
				//refresh token still valid; renew access token
				const access_token = await generateAccesToken(user_name)
				return res.status(200).json({ refresh_token: refresh_token, access_token: access_token })
			}
		} else {
			return res.status(403).send("Auth Failed : Invalid refresh token")
		}
	}
	catch (e) {
		return res.status(500).send(`Oops something went wrong! Error : ${e.message}`)
	}
})

//debugger to listen for any non exisisting endpoints
app.use((req, res) => {
	console.log(`No route matched: ${req.method} ${req.url}`);
	res.status(404).send("Not Found");
});
