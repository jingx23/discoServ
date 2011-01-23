package de.snertlab.discoserv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class Common {
	
	public static boolean stringIsEmpty(String s){
		if(s==null || "".equals(s)){
			return true;
		}
		return false;
	}
	
    public static Dialog createErrorDialog(Context context, String message){
    	return createAlertDialog(context, "Error", message);
    }
    
    public static Dialog createAlertDialog(Context context, String message){
    	return createAlertDialog(context, null, message);
    }
    
    public static Dialog createAlertDialog(Context context, String title, String message){
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle(title);
    	builder.setIcon(0);
    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	builder.setMessage(message);
    	builder.setCancelable(false);
    	AlertDialog alert = builder.create();
    	return alert;
    }

}
