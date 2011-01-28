package de.snertlab.discoserv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class RequestGuthabenThread extends AsyncTask<Void, Void, Void>{
	
	private static final Pattern PATTERN_GUTHABEN = Pattern.compile("Guthaben:{1}.*<b>(.*) EUR{1} </b>{1}");
	
	private boolean stop;
	private DiscoServActivity activity;
	private DiscoServSqlOpenHelper myDB;
	private View view;
	private DefaultHttpClient httpclient;
	private String benutzername;
	private String passwort;
	private ProgressDialog dialog;
	
	public RequestGuthabenThread(DiscoServActivity activity, DiscoServSqlOpenHelper myDB, View view, String benutzername, String passwort){
		this.view 	  = view;
		this.myDB	  = myDB;
		this.activity = activity;
		this.benutzername = benutzername;
		this.passwort = passwort;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try{
			httpclient = new DefaultHttpClient();				
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
	    		String html 	= makeHtmlFromResponse(response);
	    		String betrag 	= findGuthaben(html);
	    		if(betrag==null) throw new RuntimeException("Betrag konnte nicht ermittelt werden");
	    		double b = Common.formatBetragToDouble(betrag);
	    		myDB.insertNewBetrag(b);
    			activity.updateGuthabenText(b);
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
				activity.showToast(view, "Fehler beim abrufen aufgetreten");
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
    
    private String findGuthaben(String html){
    	Log.d(DiscoServActivity.LOG_TAG, "findGuthaben start");
    	String betrag = null;
    	Matcher m = PATTERN_GUTHABEN.matcher(html);
    	if(m.find()){
    		betrag = m.group(1);
    	}
    	Log.d(DiscoServActivity.LOG_TAG, "findGuthaben end");
    	return betrag;
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
}
