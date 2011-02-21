package de.snertlab.discoserv.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Guthaben implements IGuthaben{
	
	public static final DateFormat FORMAT_LETZTE_AKTUALISIERUNG = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	private String guthaben;
	private Date datum;
	private List<IPosition> listPositionen;
	private String gebuehrenVom;
	private String gebuehrenBis;
	private String tarif;

	public Guthaben(String guthaben, Date datum){
		this.listPositionen = new ArrayList<IPosition>();
		this.guthaben = guthaben;
		this.datum = datum;
	}
	
	public Guthaben(String guthaben){
		this(guthaben, null);
	}

	public String getGuthaben() {
		return guthaben;
	}

	public void setGuthaben(String guthaben) {
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
		String guthabenEur = guthaben + "Û";
		return guthabenEur;
	}

	@Override
	public List<IPosition> getListPositionenUnmodifiable() {
		return Collections.unmodifiableList(listPositionen);
	}

	public void fillListPositionen(List<IPosition> listPositionen) {
		this.listPositionen.clear();
		this.listPositionen.addAll(listPositionen);
	}

	public String getGebuehrenVom() {
		return gebuehrenVom;
	}

	public void setGebuehrenVom(String gebuehrenVom) {
		this.gebuehrenVom = gebuehrenVom;
	}

	public String getGebuehrenBis() {
		return gebuehrenBis;
	}

	public void setGebuehrenBis(String gebuehrenBis) {
		this.gebuehrenBis = gebuehrenBis;
	}

	public String getTarif(){
		return tarif;
	}
	
	public void setTarif(String tarif){
		this.tarif = tarif;
	}
}
