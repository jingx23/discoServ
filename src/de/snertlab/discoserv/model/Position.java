package de.snertlab.discoserv.model;

public class Position implements IPosition{
	
	private String brutto;
	private String netto;
	private String uSt;
	private String positionBez;

	public String getBrutto() {
		return brutto;
	}

	public void setBrutto(String brutto) {
		this.brutto = brutto;
	}

	public String getNetto() {
		return netto;
	}

	public void setNetto(String netto) {
		this.netto = netto;
	}

	public String getUSt() {
		return uSt;
	}

	public void setUSt(String uSt) {
		this.uSt = uSt;
	}

	public String getPositionBez() {
		return positionBez;
	}

	public void setPositionBez(String positionBez) {
		this.positionBez = positionBez;
	}

	
}
