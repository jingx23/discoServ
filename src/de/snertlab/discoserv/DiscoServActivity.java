package de.snertlab.discoserv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

public class DiscoServActivity extends Activity {
    
	public static final String INTENT_SMS_RECIEVE = "intent_sms_recieve";
	
    /** Called when the activity is first created. */
	
	private BroadcastReceiver smsAlarmReceiver = new BroadcastReceiver(){
	    // Display an alert that we've received a message.    
		@Override 
	    public void onReceive(Context context, Intent intent){
	    	String address 			= intent.getStringExtra("address");
//	    	String message 		   	= intent.getStringExtra("message");
//	    	String timestampMillis 	= intent.getStringExtra("timestampMillis");
	    	try{
	    		Thread.sleep(500);
	    	}catch (Exception e) {
				//nothing
			}
	        Uri deleteUri = Uri.parse("content://sms");
	        //TODO: Loeschen mit datum einbauen => funzt so noch nicht
	        //getContentResolver().delete(deleteUri, "address=? and date=?", new String[] {address, timestampMillis});
	        getContentResolver().delete(deleteUri, "address=?", new String[] {address});
	   }
	};
	private IntentFilter intentFilter; 

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_SMS_RECIEVE);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(smsAlarmReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(smsAlarmReceiver);
	}
    
    public void btnSendClickHandler(View view){
    	SmsManager sm = SmsManager.getDefault();
    	sm.sendTextMessage("0000", null, "this is a test", null, null);
    }
    
}