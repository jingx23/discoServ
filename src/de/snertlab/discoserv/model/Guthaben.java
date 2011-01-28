package de.snertlab.discoserv.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.snertlab.discoserv.Common;

public class Guthaben implements IGuthaben{
	
	private static final DateFormat FORMAT_LETZTE_AKTUALISIERUNG = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	private double guthaben;
	private Date datum;

	public Guthaben(double guthaben, Date datum){
		this.guthaben = guthaben;
		this.datum = datum;
	}
	
	public Guthaben(double guthaben){
		this(guthaben, null);
	}

	public double getGuthaben() {
		return guthaben;
	}

	public void setGuthaben(double guthaben) {
		this.guthaben = guthaben;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
	public String getDatumDisplay(){
		if(datum==null){
			return "-";
		}
		String sDate = FORMAT_LETZTE_AKTUALISIERUNG.format(datum);
		return sDate;
	}

	public String getGuthabenDisplay(){
		String guthabenEur = Common.formatBetragToDisplay(guthaben);
		return guthabenEur;
	}

	
}
