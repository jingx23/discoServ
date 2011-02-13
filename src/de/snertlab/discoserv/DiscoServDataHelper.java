package de.snertlab.discoserv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import de.snertlab.discoserv.model.Guthaben;
import de.snertlab.discoserv.model.IGuthaben;
import de.snertlab.discoserv.model.IPosition;
import de.snertlab.discoserv.model.Position;

public class DiscoServDataHelper {
	
	private static String DATA_FILE = "data.txt";

	public IGuthaben getLastGuthabenFromDb(DiscoServActivity activity) {
		try{
			if( ! checkDataFileExist(activity) ) return null;
			Guthaben guthaben = new Guthaben(null);
			List<IPosition> listPositionen = new ArrayList<IPosition>();
			BufferedReader buf = new BufferedReader( new InputStreamReader(activity.openFileInput(DATA_FILE)));
			String line = null;
			int lineCount = 0;
			while((line=buf.readLine())!=null){
				if(lineCount==0){
					guthaben.setGuthaben(line);
				}else if(lineCount==1){
					guthaben.setDatum(Guthaben.FORMAT_LETZTE_AKTUALISIERUNG.parse(line));
				}else if(lineCount > 1){
					String[] posFields = line.split(";");
					Position pos = new Position();
					for (int i = 0; i < posFields.length; i++) {
						String posField = posFields[i];
						if(i==0){
							pos.setPositionBez(posField);
						}else if(i==1){
							pos.setNetto(posField);
						}else if(i==2){
							pos.setUSt(posField);
						}else if(i==3){
							pos.setBrutto(posField);
						}else{
							throw new RuntimeException("undefined posField: " + i + " " + posField);
						}
					}
					listPositionen.add(pos);
				}else{
					throw new RuntimeException("Undefined line count: " + lineCount);
				}
				lineCount++;
			}
			buf.close();
			guthaben.fillListPositionen(listPositionen);
			return guthaben;
		}catch (Exception e) {
			Log.e(DiscoServActivity.LOG_TAG, "",e);
			throw new RuntimeException(e);
		}
	}

	public void saveGuthaben(DiscoServActivity activity, IGuthaben guthaben){
		try{
			FileOutputStream fOut = activity.openFileOutput(DATA_FILE, Activity.MODE_PRIVATE);
			BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fOut));
			buf.write(guthaben.getGuthaben() + "\n");
			buf.write(Guthaben.FORMAT_LETZTE_AKTUALISIERUNG.format(guthaben.getDatum())+ "\n");
			for (IPosition position : guthaben.getListPositionenUnmodifiable()) {
				buf.write( position.getPositionBez() + ";"
						   + position.getNetto() 	 + ";"
						   + position.getUSt() 		 + ";"
						   + position.getBrutto()	 + "\n"
						 );
			}
			buf.flush();
			buf.close();
		}catch (Throwable e) {
			Log.e(DiscoServActivity.LOG_TAG, "",e);
			throw new RuntimeException(e);
		}
	}
	
	private boolean checkDataFileExist(DiscoServActivity activity){
		String[] files = activity.fileList();
		for (String filename : files) {
			if(filename.equals(DATA_FILE)){
				return true;
			}
		}
		return false;
	}

}
