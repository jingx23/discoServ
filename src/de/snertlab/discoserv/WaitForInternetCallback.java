package de.snertlab.discoserv;

import android.app.Activity;

/**
 * This code was taken from http://bjdodson.blogspot.com/2009/10/checking-for-internet-availability-on.html
 * THANK YOU!
 *
 */
public abstract class WaitForInternetCallback {
	protected Activity mActivity;
	 
	public WaitForInternetCallback(Activity activity) {
		 mActivity=activity;
	 }
	 
	 public abstract void onConnectionSuccess();
	 public abstract void onConnectionFailure();
}
