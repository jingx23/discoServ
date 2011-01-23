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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DiscoServActivity extends Activity {
	
	public static final String LOG_TAG = "DiscoServ";
	
	private static Pattern PATTERN_GUTHABEN = Pattern.compile("Guthaben:{1}.*<b>(.*) EUR{1} </b>{1}");
	private TextView txtViewGuthaben;
	private Button btnRequestGuthaben;
	private String betrag;
	private RequestGuthabenThread threadRequestBeitrag;
	private String benutzername;
	private String passwort;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txtViewGuthaben = (TextView) findViewById(R.id.txtViewGuthaben);
        btnRequestGuthaben = (Button) findViewById(R.id.Button01);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		benutzername = prefs.getString(SettingsPreferencesActivity.KEY_USERNAME, "");
		passwort = prefs.getString(SettingsPreferencesActivity.KEY_PASSWORD, "");
		if(isBenutzernamePasswortEmpty()){
			StringBuilder sb = new StringBuilder("Benutzername und Passwort müssen noch festgelegt werden");
			Dialog d = Common.createAlertDialog(txtViewGuthaben.getContext(), sb.toString());
			d.show();
			btnRequestGuthaben.setEnabled(false);
		}else{
			btnRequestGuthaben.setEnabled(true);
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.d(LOG_TAG, "onDestroy");
		if(threadRequestBeitrag!=null && threadRequestBeitrag.isAlive()){
			Log.d(LOG_TAG, "Stoppe threadRequestBeitrag");
			threadRequestBeitrag.stopMe();
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		 switch (item.getItemId()) {
		    case R.id.menu_settings:
		    	startActivity(new Intent(this, SettingsPreferencesActivity.class));
		        return true;
		    case R.id.menu_quit:
		        quit();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
		    }
	}
	
	private void quit(){
		this.finish();
	}
	    
    public void btnRequestClickHandler(final View view) {
    	Log.d(LOG_TAG, "btnRequestClickHandler");
    	if(threadRequestBeitrag!=null && threadRequestBeitrag.isAlive()) return;
    	//TODO:Pruefen ob internet verfuegbar
//    	ConnectivityManager conManager = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//    	conManager.requestRouteToHost(ConnectivityManager., hostAddress)
    	betrag = "";
    	txtViewGuthaben.setText("");
    	threadRequestBeitrag = new RequestGuthabenThread(view);
    	threadRequestBeitrag.start();
    }
    
    
    private void showToast(final View view, final String text){
    	Log.d(LOG_TAG, "showToast");
		this.runOnUiThread(new Runnable() {
		    public void run() {
		    	Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
		    }
		});
    }
    
    private void updateBetragText(final String betrag){
    	Log.d(LOG_TAG, "updateBetragText");
		this.runOnUiThread(new Runnable() {
		    public void run() {
		    	txtViewGuthaben.setText(betrag + "€");
		    }
		});    	
    }

    private String makeHtmlFromResponse(HttpResponse response){
    	Log.d(LOG_TAG, "makeHtmlFromResponse start");
    	try{
	    	StringBuilder sb = new StringBuilder();
	    	BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String readLine;
	    	while(((readLine = br.readLine()) != null)) {
	    		sb.append(readLine);
	    	}
	    	br.close();
	    	Log.d(LOG_TAG, "makeHtmlFromResponse end");
	    	return sb.toString();
    	}catch (Exception e) {
    		Log.e(LOG_TAG, "",e);
    		throw new RuntimeException(e);
		}
    }
    
    private String findGuthaben(String html){
    	Log.d(LOG_TAG, "findGuthaben start");
    	String betrag = null;
    	Matcher m = PATTERN_GUTHABEN.matcher(html);
    	if(m.find()){
    		betrag = m.group(1);
    	}
    	Log.d(LOG_TAG, "findGuthaben end");
    	return betrag;
    }
    
    private class RequestGuthabenThread extends Thread{
    	private boolean stop;
    	private View view;
    	private DefaultHttpClient httpclient;
    	
    	public RequestGuthabenThread(View view){
    		this.view = view;
    	}
    	
    	@Override
    	public void run() {
			try{
				httpclient = new DefaultHttpClient();
				Log.d(LOG_TAG, "requestBetrag thread startet");
				showToast(view, "Verbinde bitte warten...");
		    	List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		    	HttpPost httpost = new HttpPost("https://service.discoplus.de/frei/LOGIN");
		    	nvps.add(new BasicNameValuePair("credential_0", benutzername));
		    	nvps.add(new BasicNameValuePair("credential_1", passwort));
		    	nvps.add(new BasicNameValuePair("destination", "/discoplus/index3.php"));
		    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		    	HttpResponse response = httpclient.execute(httpost);
		    	int statusCode = response.getStatusLine().getStatusCode();
		    	if(HttpStatus.SC_OK==statusCode){
		    		showToast(view, "Status OK");
		    		String html 	= makeHtmlFromResponse(response);
		    		betrag 	= findGuthaben(html);
	    			Log.d(LOG_TAG, "mHandler.post");
	    			updateBetragText(betrag);
		    	}else if(HttpStatus.SC_FORBIDDEN==statusCode){
		    		StringBuilder sb = new StringBuilder("Fehler Benutzername oder Passwort falsch");
		    		Log.w(LOG_TAG, sb.toString());
		    		showToast(view, sb.toString());		    		
		    	}else{
		    		StringBuilder sb = new StringBuilder("Fehler falscher HTTP Status: " + statusCode);
		    		Log.w(LOG_TAG, sb.toString());
		    		showToast(view, sb.toString());
		    	}
				httpclient.getConnectionManager().shutdown();
			}catch (Exception e) {
				if(stop && "Request aborted".equals(e.getMessage())){
					return; //ignore
				}
				Log.e(LOG_TAG, "", e);
				showToast(view, "Fehler beim Aufbau der Verbindung");
			}
			Log.d(LOG_TAG, "requestBetrag thread end");
		}
    	
    	public void stopMe(){
    		Log.d(LOG_TAG, "stopMe");
			httpclient.getConnectionManager().shutdown();
    		this.stop = true;
    		this.interrupt();
    	}
    }
    
    private boolean isBenutzernamePasswortEmpty(){
    	return (Common.stringIsEmpty(benutzername) || Common.stringIsEmpty(passwort));
    }
}