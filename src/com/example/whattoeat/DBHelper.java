package com.example.whattoeat;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	
	private final static String DATABASE_NAME = "history.db";
    private final static int DATABASE_VERSION = 1;
    
	public DBHelper(Context context) {
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String INIT_TABLE = "CREATE TABLE history (" +
                "_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"date VARCHAR, " +
                "restaurant VARCHAR, " +
                "menu VARCHAR, " +
                "photo VARCHAR, " +
                "rating INTEGER, " +
                "comment TEXT" +
                ");"; 
		Log.d("emo", "init db");
		db.execSQL(INIT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS history");
		onCreate(db);
	}

}
