package de.snertlab.discoserv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import de.snertlab.discoserv.model.Guthaben;
import de.snertlab.discoserv.model.IPosition;

public class RequestGuthabenThread extends AsyncTask<Void, Void, Void>{
	
	private static final int CON_TIMEOUT 	= (60 * 1000);
	private static final int SOCKET_TIMEOUT = (60 * 1000);
	
	private boolean stop;
	private DiscoServActivity activity;
	private DiscoServDataHelper myDB;
	private View view;
	private DefaultHttpClient httpclient;
	private String benutzername;
	private String passwort;
	private ProgressDialog dialog;
	
	public RequestGuthabenThread(DiscoServActivity activity, DiscoServDataHelper myDB, View view, String benutzername, String passwort){
		this.view 	  = view;
		this.myDB	  = myDB;
		this.activity = activity;
		this.benutzername = benutzername;
		this.passwort = passwort;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try{
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, CON_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
			httpclient = new MyHttpClient(httpParams);
			Log.d(DiscoServActivity.LOG_TAG, "requestBetrag thread startet");
	    	List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    	HttpPost httpost = new HttpPost("https://service.discoplus.de/frei/LOGIN");
	    	nvps.add(new BasicNameValuePair("credential_0", benutzername));
	    	nvps.add(new BasicNameValuePair("credential_1", passwort));
	    	nvps.add(new BasicNameValuePair("destination", "/discoplus/index3.php"));
	    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	    	HttpResponse response = httpclient.execute(httpost);
	    	int statusCode = response.getStatusLine().getStatusCode();
	    	if(HttpStatus.SC_OK==statusCode){
	    		HttpGet httpGet = new HttpGet("https://service.discoplus.de/discoplus/prepaid.php?action=rechansicht");
	    		response = httpclient.execute(httpGet);
	    		statusCode = response.getStatusLine().getStatusCode();
	    		if(HttpStatus.SC_OK!=statusCode){ //TODO: sauberer
		    		StringBuilder sb = new StringBuilder("Fehler falscher HTTP Status: " + statusCode);
		    		Log.w(DiscoServActivity.LOG_TAG, sb.toString());
		    		activity.showToast(view, sb.toString());
	    		}else{
		    		String html 	= makeHtmlFromResponse(response);
		    		String betrag 	= DiscoParser.findGuthaben(html);
		    		String tarif	= DiscoParser.findTarif(html);
		    		List<IPosition> listPositionen = DiscoParser.parsePositionen(html);
		    		if(betrag==null) throw new RuntimeException("Betrag konnte nicht ermittelt werden");
		    		Guthaben guthaben = new Guthaben(betrag, new Date());
		    		guthaben.setTarif(tarif);
		    		guthaben.fillListPositionen(listPositionen);
		    		DiscoParser.parseGebuehrenVonBis(html, guthaben);
		    		myDB.saveGuthaben(activity, guthaben);
	    			activity.updateGuthabenLabels(guthaben);
	    		}
	    	}else if(HttpStatus.SC_FORBIDDEN==statusCode){
	    		StringBuilder sb = new StringBuilder("Fehler Benutzername oder Passwort falsch");
	    		activity.showToast(view, sb.toString());
	    		Log.w(DiscoServActivity.LOG_TAG, sb.toString());
	    	}else{
	    		StringBuilder sb = new StringBuilder("Fehler falscher HTTP Status: " + statusCode);
	    		Log.w(DiscoServActivity.LOG_TAG, sb.toString());
	    		activity.showToast(view, sb.toString());
	    	}
			httpclient.getConnectionManager().shutdown();
		}catch (Exception e) {
			if(stop && "Request aborted".equals(e.getMessage())){
				//ignore
			}else{
				Log.e(DiscoServActivity.LOG_TAG, "", e);
				activity.showToast(view, "Fehler beim abrufen aufgetreten: " + e.getMessage());
			}
		}
		Log.d(DiscoServActivity.LOG_TAG, "requestBetrag thread end");
		return null;
	}
	
	public void stopMe(){
		Log.d(DiscoServActivity.LOG_TAG, "stopMe");
		httpclient.getConnectionManager().shutdown();
		this.stop = true;
	}
	
    private String makeHtmlFromResponse(HttpResponse response){
    	Log.d(DiscoServActivity.LOG_TAG, "makeHtmlFromResponse start");
    	try{
	    	StringBuilder sb = new StringBuilder();
	    	BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String readLine;
	    	while(((readLine = br.readLine()) != null)) {
	    		sb.append(readLine);
	    	}
	    	br.close();
	    	Log.d(DiscoServActivity.LOG_TAG, "makeHtmlFromResponse end");
	    	return sb.toString();
    	}catch (Exception e) {
    		Log.e(DiscoServActivity.LOG_TAG, "",e);
    		throw new RuntimeException(e);
		}
    }
        
    @Override
    protected void onPostExecute(Void result) {
    	dialog.dismiss();
    }
    
    @Override
    protected void onPreExecute() {
    	dialog = ProgressDialog.show(activity, "", "Bitte warten...", true);
    }
    
    @Override
    protected void onCancelled() {
    	stopMe();
    	super.onCancelled();
    }

	public class MyHttpClient extends DefaultHttpClient {
		public MyHttpClient(HttpParams httpParams){
			super(httpParams);
		}
		
		@Override
		protected ClientConnectionManager createClientConnectionManager() {
			try{
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				registry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
				return new SingleClientConnManager(getParams(), registry);
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
