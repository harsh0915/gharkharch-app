import mysql from "mysql2"

export const DB_NAME = {
	DB_NAME: 'gharkharch',
};

export const  TABLES = {
	USERS: 'users',
	CATEGORIES: 'categories',
	SUB_CATEGORIES: 'sub_categories',
	EXPENSE: 'expense',
};

export const COLUMNS = {
	USER_ID: 'user_id',
	USER_NAME: 'user_name',
	USER_PSSWRD: 'user_psswrd',

	CAT_ID: 'cat_id',
	CAT_NAME: 'cat_name',

	SUB_CAT_ID: 'sub_cat_id',
	SUB_CAT_NAME: 'sub_cat_name',
	UNDER_CAT_ID: 'under_cat_id',

	EXPENSE_ID: 'expense_id',
	EXPENSE_DATE: 'expense_date',
	EXPENSE_MODE: 'expense_mode',
	EXPENSE_REMARK: 'expense_remark',
	UNDER_SUB_CAT_ID: 'under_sub_cat_id',
};

const pool = mysql.createPool({
	host : '127.0.0.1',
	user : 'gharkharch_admin',
	password : 'swatimehra',
	database : 'gharkharch',
	queueLimit: '0'
}).promise()

export async function getCategories(user_id){
	const result = await pool.query(`select * from ${TABLES.CATEGORIES} where ${COLUMNS.USER_ID} = ?`,[user_id])
	return result[0];
}

export async function getSubCategories(){
	const result =  await pool.query(`select * from ${TABLES.SUB_CATEGORIES}`)
	return result[0];
}

export async function getExpenses(user_id){
	const result =  await pool.query(`select * from ${TABLES.EXPENSE} where ${COLUMNS.USER_ID} = ?`,[user_id])
	return result[0];
}

