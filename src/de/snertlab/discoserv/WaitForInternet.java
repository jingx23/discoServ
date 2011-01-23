package de.snertlab.discoserv;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
/**
 * This code was taken from http://bjdodson.blogspot.com/2009/10/checking-for-internet-availability-on.html
 * THANK YOU!
 *
 */
public class WaitForInternet {
	/**
	  * Check for internet connectivity.
	  * The calling context must have permission to
	  * access the device's network state.
	  *
	  * If the calling context does not have permission, an exception is thrown.
	  *
	  * @param WaitForInternetCallback
	  * @return
	  */
	public static void setCallback(final WaitForInternetCallback callback) {  
		final ConnectivityManager connMan = (ConnectivityManager) callback.mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		final AtomicBoolean isConnected = new AtomicBoolean(connMan.getActiveNetworkInfo() != null && connMan.getActiveNetworkInfo().isConnected());
		if (isConnected.get()) {
			callback.onConnectionSuccess();
			return;
		}
	  
		final AtomicBoolean isRetrying = new AtomicBoolean(true);

		/* dialog */
		final AlertDialog.Builder connDialog = new AlertDialog.Builder(callback.mActivity);
		connDialog.setTitle(R.string.dialogWaitForInternetTitle);
		connDialog.setMessage(R.string.dialogWaitForInternetMessage);
		connDialog.setPositiveButton(R.string.dialogWaitForInternetBtnRetry, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				synchronized (isRetrying) {
					isRetrying.notify();
				}
			}
		});
		connDialog.setNegativeButton(R.string.dialogWaitForInternetBtnAbort, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				synchronized (isRetrying) {
					isRetrying.set(false);
					isRetrying.notify();
				}
			}
		});

		new Thread() {
			public void run() {
				while (!isConnected.get() && isRetrying.get()) {
					callback.mActivity.runOnUiThread(new Thread() {
						@Override
						public void run() {
							connDialog.show();
						}
					});
					synchronized (isRetrying) {
						try {
							isRetrying.wait();
						} catch (InterruptedException e) {
							//Log.w("WaitForInternet", "Error waiting for retry lock", e);
						}
					}

					isConnected.set( (connMan.getActiveNetworkInfo() != null && connMan.getActiveNetworkInfo().isConnected()) );
				}
				if (isConnected.get()) {
					callback.onConnectionSuccess();
				} else {
					callback.onConnectionFailure();
				}
			}
		}.start();
	}
}