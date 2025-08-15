import mysql from "mysql2"
import dotenv from "dotenv"

dotenv.config();

export const  TABLES = {
	USERS: 'users',
};

export const COLUMNS = {
	USER_ID: 'user_id',
	USER_NAME: 'user_name',
	USER_PSSWRD: 'user_psswrd',
};

const pool = mysql.createPool({
	host : process.env.HOST,
	user : process.env.DB_USER,
	password : process.env.DB_PASS,
	database : process.env.DB_NAME,
	queueLimit: '0'
}).promise()

export async function getUser(user_id){
	const result = await pool.query(`select * from ${TABLES.USERS} where ${COLUMNS.USER_ID} = ?`,[user_id])
	return result[0];
}
