package de.snertlab.discoserv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
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
	private static final String PACKAGE = "de.snertlab.discoserv";
	private static final String KEY_STATE_BETRAG = "betrag";
	
	private TextView txtViewGuthaben;
	private Button btnRequestGuthaben;
	private RequestGuthabenThread threadRequestBeitrag;
	private String benutzername;
	private String passwort;
	private boolean inetConnectionSuccess;
	
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }
	
	private void init(){
        txtViewGuthaben = (TextView) findViewById(R.id.txtViewGuthaben);
        btnRequestGuthaben = (Button) findViewById(R.id.Button01);
        updateGuthabenText("0,00");
        this.setTitle( this.getTitle() + "  v" + getVersionInfo());
        doCheckInternetConnection();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		benutzername = prefs.getString(SettingsPreferencesActivity.KEY_USERNAME, "");
		passwort = prefs.getString(SettingsPreferencesActivity.KEY_PASSWORD, "");
		if(isBenutzernamePasswortEmpty()){
			StringBuilder sb = new StringBuilder("Benutzername und Passwort m�ssen noch festgelegt werden");
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
		if(threadRequestBeitrag!=null && AsyncTask.Status.RUNNING.equals(threadRequestBeitrag.getStatus())){
			Log.d(LOG_TAG, "Stoppe threadRequestBeitrag");
			threadRequestBeitrag.cancel(true);
			threadRequestBeitrag = null;
			System.gc();
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
    	if(threadRequestBeitrag!=null && AsyncTask.Status.RUNNING.equals(threadRequestBeitrag.getStatus())) return;
    	doCheckInternetConnection();
	    if(inetConnectionSuccess){
	    	threadRequestBeitrag = new RequestGuthabenThread(this, view, benutzername, passwort);
	    	threadRequestBeitrag.execute();
	    }
    }
    
    
    public void showToast(final View view, final String text){
    	Log.d(LOG_TAG, "showToast");
		this.runOnUiThread(new Runnable() {
		    public void run() {
		    	Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
		    }
		});
    }
    
    public void updateGuthabenText(final String betrag){
    	Log.d(LOG_TAG, "updateBetragText");
		this.runOnUiThread(new Runnable() {
		    public void run() {
		    	txtViewGuthaben.setText("Guthaben: " + betrag + "�");
		    }
		});    	
    }
    
    private boolean isBenutzernamePasswortEmpty(){
    	return (Common.stringIsEmpty(benutzername) || Common.stringIsEmpty(passwort));
    }
    
    private String getVersionInfo(){
    	PackageInfo pInfo  = null;
    	String versionName = null;
    	try {
    		pInfo = getPackageManager().getPackageInfo(PACKAGE, PackageManager.GET_META_DATA);
    		versionName = pInfo.versionName;
    	} catch (NameNotFoundException e) {
    		Log.e(LOG_TAG, "", e);
    	}
    	return versionName;
    }
    
    private void doCheckInternetConnection(){
    	WaitForInternetCallback waitForInternetCallback = new WaitForInternetCallback(this) {
	    	public void onConnectionSuccess() {
	    		inetConnectionSuccess = true;
	    	 }
	    	public void onConnectionFailure() {
	    		inetConnectionSuccess = false;
	    	}
	    };
	    try {
	    	WaitForInternet.setCallback(waitForInternetCallback);
	    } catch (SecurityException e) {
	    	Log.w(LOG_TAG,"Netzwerk Status kann nicht gepr�ft werden", e);
	    	waitForInternetCallback.onConnectionSuccess();
	    }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putString(KEY_STATE_BETRAG, txtViewGuthaben.getText().toString());
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	String guthaben = savedInstanceState.getString(KEY_STATE_BETRAG);
    	updateGuthabenText(guthaben);
    }

}