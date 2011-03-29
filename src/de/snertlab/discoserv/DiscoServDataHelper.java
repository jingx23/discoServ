package de.snertlab.discoserv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import de.snertlab.discoserv.model.Guthaben;
import de.snertlab.discoserv.model.IGuthaben;
import de.snertlab.discoserv.model.IPosition;
import de.snertlab.discoserv.model.Position;

public class DiscoServDataHelper {
	private static final String DATA_FORMAT_PREFIX	= "V";
	private static final int DATA_FORMAT_VERSION	= 1;
	private static final String DATA_FILE = "data.txt";

	public IGuthaben getLastGuthabenFromDb(Context activity) {
		try{
			patchDataFileFromVersion_1_2_0(activity);
			if( ! checkDataFileExist(activity)) return null;
			Guthaben guthaben = new Guthaben(null);
			List<IPosition> listPositionen = new ArrayList<IPosition>();
			BufferedReader buf = new BufferedReader( new InputStreamReader(activity.openFileInput(DATA_FILE)));
			String line = null;
			int lineCount = 0;
			while((line=buf.readLine())!=null){
				if(lineCount==0){
					//Version
				}else if(lineCount==1){
					guthaben.setTarif(line);
				}else if(lineCount==2){
					guthaben.setGuthaben(line);
				}else if(lineCount==3){
					guthaben.setDatum(Guthaben.FORMAT_LETZTE_AKTUALISIERUNG.parse(line));
				}else if(lineCount==4){
					guthaben.setGebuehrenVom(line);
				}else if(lineCount==5){
					guthaben.setGebuehrenBis(line);
				}else if(lineCount > 5){
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
				}else if(lineCount > 1){
					
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

	public void saveGuthaben(Context activity, IGuthaben guthaben){
		try{
			FileOutputStream fOut = activity.openFileOutput(DATA_FILE, Activity.MODE_PRIVATE);
			BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fOut));
			buf.write(DATA_FORMAT_PREFIX + DATA_FORMAT_VERSION+ "\n");
			buf.write(guthaben.getTarif() + "\n");
			buf.write(guthaben.getGuthaben() + "\n");
			buf.write(Guthaben.FORMAT_LETZTE_AKTUALISIERUNG.format(guthaben.getDatum())+ "\n");
			buf.write(guthaben.getGebuehrenVom() + "\n");
			buf.write(guthaben.getGebuehrenBis() + "\n");
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
	
	private boolean checkDataFileExist(Context activity){
		String[] files = activity.fileList();
		for (String filename : files) {
			if(filename.equals(DATA_FILE)){
				return true;
			}
		}
		return false;
	}
	
	private void patchDataFileFromVersion_1_2_0(Context activity) throws IOException, ParseException{
		if( ! checkDataFileExist(activity)) return;
		BufferedReader buf = new BufferedReader( new InputStreamReader(activity.openFileInput(DATA_FILE)));
		String version = buf.readLine();
		if(version.startsWith(DATA_FORMAT_PREFIX)){
			version = version.substring(version.indexOf(DATA_FORMAT_PREFIX)+1);
			if(Integer.parseInt(version)>=DATA_FORMAT_VERSION){
				buf.close();
				return;
			}
		}
		buf.close();
		buf = new BufferedReader( new InputStreamReader(activity.openFileInput(DATA_FILE)));
		Guthaben guthaben = new Guthaben(null);
		List<IPosition> listPositionen = new ArrayList<IPosition>();
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
		guthaben.setTarif("-");
		guthaben.setGebuehrenVom("-");
		guthaben.setGebuehrenBis("-");
		guthaben.fillListPositionen(listPositionen);
		saveGuthaben(activity, guthaben);
	}
}
