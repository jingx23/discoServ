package de.snertlab.discoserv;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class Common {
	
	private static final DecimalFormat DECIMAL_FORMAT = ((DecimalFormat)NumberFormat.getInstance(Locale.GERMANY));
	static{
		DECIMAL_FORMAT.applyPattern("#,##0.00");
	}
	
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
    
    public static double formatBetragToDouble(String betrag){
        try{
        	double d = DECIMAL_FORMAT.parse(betrag).doubleValue();
        	return d;
        }catch (ParseException e) {
        	throw new RuntimeException(e);
		}
    }
    
    public static String formatBetragToDisplay(double betrag){
    	String s = DECIMAL_FORMAT.format(betrag);
    	return s + " \u20AC";
    }

}
