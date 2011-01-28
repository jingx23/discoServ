package de.snertlab.discoserv.model;

import java.util.Date;

public class Guthaben implements IGuthaben{

	private double guthaben;
	private Date datum;
	
	public Guthaben(double guthaben, Date datum){
		this.guthaben = guthaben;
		this.datum = datum;
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
	
}
