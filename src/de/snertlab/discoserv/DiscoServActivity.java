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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DiscoServActivity extends Activity {
	
	public static final String LOG_TAG = "DiscoServ";
	
	private static Pattern PATTERN_GUTHABEN = Pattern.compile("Guthaben:{1}.*<b>(.*) EUR{1} </b>{1}");
	private DefaultHttpClient httpclient;
	private TextView txtViewGuthaben;
	private Handler mHandler;
	private String betrag;
	
    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txtViewGuthaben = (TextView) findViewById(R.id.txtViewGuthaben);
    	httpclient = new DefaultHttpClient();
    	mHandler = new Handler();
    }
	    
    public void btnRequestClickHandler(View view) {
    	//TODO:Pruefen ob internet verfuegbar
//    	ConnectivityManager conManager = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//    	conManager.requestRouteToHost(ConnectivityManager., hostAddress)
    	betrag = "";
    	txtViewGuthaben.setText("");
    	Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
			    	List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			    	HttpPost httpost = new HttpPost("https://service.discoplus.de/frei/LOGIN");
			    	nvps.add(new BasicNameValuePair("credential_0", "mobileNr"));
			    	nvps.add(new BasicNameValuePair("credential_1", "password"));
			    	nvps.add(new BasicNameValuePair("destination", "/discoplus/index3.php"));
			    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			    	HttpResponse response = httpclient.execute(httpost);
			    	if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){
			    		String html 	= makeHtmlFromResponse(response);
			    		betrag 	= findGuthaben(html);
			    		if(betrag==null){
			    			Log.e(LOG_TAG, "Betrag ist leer");
			    		}else{
			    			mHandler.post(mUpdateResults);
			    		}
			    	}
				}catch (Exception e) {
					Log.e(LOG_TAG, "", e);
				}
			}
		});
    	thread.start();
    }

    private void updateResultsInUi(){
    	txtViewGuthaben.setText(betrag + "Û");
    }
    
    private String makeHtmlFromResponse(HttpResponse response){
    	try{
	    	StringBuilder sb = new StringBuilder();
	    	BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	String readLine;
	    	while(((readLine = br.readLine()) != null)) {
	    		sb.append(readLine);
	    	}
	    	br.close();
	    	return sb.toString();
    	}catch (Exception e) {
    		Log.e(LOG_TAG, "",e);
		}
    	return "";
    }
    
    private String findGuthaben(String html){
    	String betrag = null;
    	Matcher m = PATTERN_GUTHABEN.matcher(html);
    	if(m.find()){
    		betrag = m.group(1);
    	}    		
    	return betrag;
    }
    
}