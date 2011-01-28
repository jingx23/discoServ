package de.snertlab.discoserv;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiscoServSqlOpenHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME 	 = "discoServDB";
	private static final int DATABASE_VERSION 	 = 1;

	public DiscoServSqlOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE guthaben (id INTEGER PRIMARY KEY AUTOINCREMENT, datum smalldatetime, betrag DOUBLE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//nothing
	}

	public void insertNewBetrag(double betrag) {
		getWritableDatabase().execSQL("INSERT INTO guthaben (datum, betrag) VALUES(" + new Date().getTime() + "," + betrag + ");");		
	}

	public double getLastBetragFromDb() {
		double betrag=0;
		Cursor c = getReadableDatabase().rawQuery("SELECT * FROM guthaben", null);
		boolean last = c.moveToLast();
		if(last){
			betrag = c.getDouble(c.getColumnIndex("betrag"));
		}
		c.close();
		return betrag;
	}


}
