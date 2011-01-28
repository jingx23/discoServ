package de.snertlab.discoserv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.snertlab.discoserv.model.Guthaben;
import de.snertlab.discoserv.model.IGuthaben;

public class DiscoServSqlOpenHelper extends SQLiteOpenHelper {
	
	private static final DateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String DATABASE_NAME 	 = "discoServDB";
	private static final int DATABASE_VERSION 	 = 1;

	public DiscoServSqlOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE guthaben (id INTEGER PRIMARY KEY AUTOINCREMENT, datum DATETIME, betrag DOUBLE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//nothing
	}

	public IGuthaben insertNewGuthaben(double dGuthaben) {
		//TODO: Vorerst nicht mehrere Eintraege erzeugen ist noch nicht sicher ob dafuer verwendung besteht
		IGuthaben guthaben = new Guthaben(dGuthaben, new Date());
		boolean entriesFound = false;
		int firstId = 0;
		Cursor c = getReadableDatabase().rawQuery("SELECT * FROM guthaben where id=1", null);
		c.moveToFirst();
		if(c.isFirst()){
			entriesFound = true;
			firstId = c.getInt(c.getColumnIndex("id"));
		}
		c.close();
		if(entriesFound){
			getWritableDatabase().execSQL("UPDATE guthaben SET datum=datetime('" + ISO8601FORMAT.format(guthaben.getDatum()) + "'), betrag="+ guthaben.getGuthaben() +" WHERE id="+firstId+";");			
		}else{
			getWritableDatabase().execSQL("INSERT INTO guthaben (datum, betrag) VALUES(datetime('" + ISO8601FORMAT.format(guthaben.getDatum()) + "')," + guthaben.getGuthaben() + ");");	
		}
		
		return guthaben;
	}

	public IGuthaben getLastGuthabenFromDb() {
		try{
			Guthaben guthaben = null;
			Cursor c = getReadableDatabase().rawQuery("SELECT * FROM guthaben", null);
			boolean last = c.moveToLast();
			if(last){
				double betrag = c.getDouble(c.getColumnIndex("betrag"));
				String sDatum = c.getString(c.getColumnIndex("datum"));
				Date datum = ISO8601FORMAT.parse(sDatum);
				guthaben = new Guthaben(betrag, datum);
			}
			c.close();
			return guthaben;
		}catch (Exception e) {
			Log.e(DiscoServActivity.LOG_TAG, "",e);
			throw new RuntimeException(e);
		}
	}


}
