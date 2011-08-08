package de.snertlab.discoserv.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.test.InstrumentationTestCase;
import de.snertlab.discoserv.DiscoParser;
import de.snertlab.discoserv.model.Guthaben;
import de.snertlab.discoserv.model.IPosition;

public class DiscoParserTest extends InstrumentationTestCase {

	private String html;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
    	StringBuilder sb = new StringBuilder();

    	InputStream is = getInstrumentation().getContext().getResources().openRawResource(R.raw.discotel);
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	String readLine;
    	while(((readLine = br.readLine()) != null)) {
    		sb.append(readLine);
    	}
    	br.close();
    	is.close();
    	html = sb.toString();
	}
	
	public void test_parse() throws IOException{
		String guthaben = DiscoParser.findGuthaben(html);
		String tarif = DiscoParser.findTarif(html);
		List<IPosition> listPositionen = DiscoParser.parsePositionen(html);
		
		Guthaben objGuthaben = new Guthaben(guthaben);
		DiscoParser.parseGebuehrenVonBis(html, objGuthaben);
		
		
		assertEquals("discoPlus 7,5Ct.", tarif);
		assertEquals("3,34", guthaben);
		assertEquals(3, listPositionen.size());
	}
	
}
