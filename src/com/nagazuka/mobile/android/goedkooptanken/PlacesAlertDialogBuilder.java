package com.nagazuka.mobile.android.goedkooptanken;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class PlacesAlertDialogBuilder {

	public static AlertDialog createDefaultExceptionAlert(Context context, String message, DialogInterface.OnClickListener positiveListener) {
		AlertDialog.Builder aDialog = new AlertDialog.Builder(context)
		.setTitle(R.string.error_alert_title)
		.setMessage(message)
		.setPositiveButton(R.string.error_alert_pos_button, positiveListener);
		
		return aDialog.create();
	}
	
	public static AlertDialog createSettingsExceptionAlert(Context context, String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
		
		
		AlertDialog.Builder aDialog = new AlertDialog.Builder(context)
		.setTitle(R.string.error_alert_title)
		.setMessage(message)
		.setNegativeButton(R.string.error_alert_neg_button, negativeListener)
		.setPositiveButton(R.string.error_alert_settings_button, positiveListener);
		
		return aDialog.create();
	}
	
	
}
