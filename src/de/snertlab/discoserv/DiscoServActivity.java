package de.snertlab.discoserv;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

public class DiscoServActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void btnSendClickHandler(View view){
    	SmsManager sm = SmsManager.getDefault();
    	sm.sendTextMessage("0000", null, "this is a test", null, null);
    }
}