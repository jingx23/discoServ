package de.snertlab.discoserv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import de.snertlab.discoserv.model.Guthaben;
import de.snertlab.discoserv.model.IPosition;
import de.snertlab.discoserv.model.Position;

public class DiscoParser {
	
	private static final Pattern PATTERN_GUTHABEN   = Pattern.compile("prepaid Guthaben.+?<font[^>]+>(.+?)</a>");
	private static final Pattern PATTERN_POSITIONEN = Pattern.compile("(?i)<a href=\"#\"[^>]+>(.+?)</a></td>(.+?)<td class=vcell[^>]+>(.+?)</td>(.+?)<td class=vcell[^>]+>(.+?)</td>(.+?)<td class=vcell[^>]+>(.+?)</td>");
	private static final Pattern PATTERN_GEBUEHREN_ZEITRAUM = Pattern.compile("<b><u>.+? vom ([0-9]{1,2}.[0-9]{1,2}.[0-9]{4}) bis ([0-9]{1,2}.[0-9]{1,2}.[0-9]{4})</u></b>");
	private static final Pattern PATTERN_TARIF 				= Pattern.compile("<b>Tarif:</b><br>(.+?)</td>");
	private static final SimpleDateFormat SDFCURRMONTHYEAR  = new SimpleDateFormat("MMyy");

	
    public static String findGuthaben(String html){
    	Log.d(DiscoServActivity.LOG_TAG, "findGuthaben start");
    	String betrag = null;
    	Matcher m = PATTERN_GUTHABEN.matcher(html);
    	if(m.find()){
    		betrag = m.group(1);
    		betrag = betrag.trim();
    		betrag = betrag.replace(".", ",");
    	}
    	Log.d(DiscoServActivity.LOG_TAG, "findGuthaben end");
    	return betrag;
    }
    
    public static List<IPosition> parsePositionen(String html){
    	Log.d(DiscoServActivity.LOG_TAG, "parsePositionen start");
    	List<IPosition> listPositionen = new ArrayList<IPosition>();
    	Matcher m = PATTERN_POSITIONEN.matcher(html);
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.MONTH, 1);
    	String currMonthYear = SDFCURRMONTHYEAR.format(cal.getTime());
    	while(m.find()){
    		String completeString = m.group(0).trim();
    		int indexOfTable = completeString.indexOf("&Table=");
    		int indexOfInvertedComma = completeString.indexOf("'", indexOfTable);
    		String sDate = completeString.substring(indexOfInvertedComma-4, indexOfInvertedComma);
    		if( ! currMonthYear.equals(sDate) ) continue;
    		Position pos = new Position();
    		pos.setPositionBez(m.group(1).trim());
    		pos.setNetto(m.group(3).trim());
    		pos.setUSt(m.group(5).trim());
    		pos.setBrutto(m.group(7).trim());
    		listPositionen.add(pos);
    	}
    	Log.d(DiscoServActivity.LOG_TAG, "parsePositionen end");
    	return listPositionen;
    }
    
    public static void parseGebuehrenVonBis(String html, Guthaben guthaben) {
		Log.d(DiscoServActivity.LOG_TAG, "parseGebuehrenVonBis start");
		Matcher m = PATTERN_GEBUEHREN_ZEITRAUM.matcher(html);
		if(m.find()){
			String datumVom = m.group(1);
			String datumBis = m.group(2);
			guthaben.setGebuehrenVom(datumVom);
			guthaben.setGebuehrenBis(datumBis);
		}
		Log.d(DiscoServActivity.LOG_TAG, "parsePositionen end");
	}
	
    public static String findTarif(String html){
		Log.d(DiscoServActivity.LOG_TAG, "findTarif start");
		String tarif = "";
		Matcher m = PATTERN_TARIF.matcher(html);
		if(m.find()){
			tarif = m.group(1);
			tarif = tarif.trim();
		}
		Log.d(DiscoServActivity.LOG_TAG, "findTarif end");
		return tarif;
	}


}
