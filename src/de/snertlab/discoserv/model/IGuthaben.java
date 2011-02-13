package de.snertlab.discoserv.model;

import java.util.Date;
import java.util.List;

public interface IGuthaben {

	String getGuthaben();
	Date getDatum();
	String getDatumDisplay();
	String getGuthabenDisplay();
	List<IPosition> getListPositionenUnmodifiable();

}
