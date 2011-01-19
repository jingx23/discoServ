package de.snertlab.discoserv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}
		SmsMessage firstMessage = smsMessage[0];
		Intent activityIntent = new Intent(DiscoServActivity.INTENT_SMS_RECIEVE);
		activityIntent.putExtra("message", firstMessage.getDisplayMessageBody());
		activityIntent.putExtra("address", firstMessage.getDisplayOriginatingAddress());
		activityIntent.putExtra("timestampMillis", String.valueOf(firstMessage.getTimestampMillis()));
		activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.sendBroadcast(activityIntent);
	}
}
