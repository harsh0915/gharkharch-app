import mysql from "mysql2"

export const TABLES = {
	USERS: 'users',
	REFRESH_TOKENS: 'refresh_tokens'
};

export const COLUMNS = {
	//table users
	USER_ID: 'user_id',
	USER_NAME: 'username',
	USER_PSSWRD: 'password',
	//table refresh tokens
	REFRESH_TOKEN: 'token',
	IS_SUB_ACTIVE: 'is_sub_active',
	EXPIRY: 'expiry'
};

let pool

export async function initDB() {
	pool = mysql.createPool({
		host: process.env.HOST_DB,
		user: process.env.DB_USER,
		password: process.env.DB_PASS,
		database: process.env.DB_NAME,
		connectionLimit: 10,
		queueLimit: 0
	}).promise()
	console.log("DB Pool initialized")
}

export async function getUserDetails(user_name) {
	const [result] = await pool.query
		(`select * from ${TABLES.USERS} u
			left join ${TABLES.REFRESH_TOKENS} rf 
			on u.${COLUMNS.USER_NAME} = rf.${COLUMNS.USER_NAME}
			where u.${COLUMNS.USER_NAME} = ?`, [user_name])

	return result[0]
}

export async function insertPassword(password, user_name) {
	const result = await pool.query
		(`update ${TABLES.USERS} set 
		${COLUMNS.USER_PSSWRD} = ? 
		where ${COLUMNS.USER_NAME} = ?`, [password, user_name])

	return result[0].affectedRows;
}

export async function insertToken(token, user_name) {
	const result = await pool.query
		(`update ${TABLES.REFRESH_TOKENS} set 
		${COLUMNS.REFRESH_TOKEN} = ?,
		${COLUMNS.EXPIRY} = NOW() + INTERVAL 30 Day
		where	${COLUMNS.USER_NAME} = ?`, [token, user_name])

	return result[0].affectedRows;
}

export async function checkSubStatus(user_name) {
	const [result] = await pool.query
		(`select ${COLUMNS.IS_SUB_ACTIVE} from ${TABLES.REFRESH_TOKENS} 
		where ${COLUMNS.USER_NAME} = (?)`, [user_name])

	return result[0][COLUMNS.IS_SUB_ACTIVE];
}
