package com.phildatoon.at5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class AlertDialogDisplay {
	
	public AlertDialogDisplay(final Context context, String type) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setIcon(R.drawable.ic_action_warning);
        
		if (type.equals("network")) {
	        alertDialog.setTitle("Network error");
	        alertDialog.setMessage("Enable network connection to use this application.");
	
	        // sets "Yes" Button
	        alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// opens Wireless settings
	            	context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	            }
	        });
	
	        // sets "No" Button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// finishes the activity
	            	dialog.cancel();
	            }
	        });
		} else if (type.equals("gps")) {
			alertDialog.setTitle("Improve location");
	        alertDialog.setMessage("Enable GPS to accurately find your location.");
	
	        // sets "Yes" Button
	        alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// opens Location settings
	            	context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	            }
	        });
	
	        // sets "No" Button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// finishes the activity
	            	dialog.cancel();
	            }
	        });
		} else if (type.equals("both")) {
			alertDialog.setTitle("Network and GPS error");
	        alertDialog.setMessage("Enable your network connection and GPS to " +
	        		"use this app and to accurately find your location.");
	
	        // sets "Yes" Button
	        alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// opens Settings
	            	context.startActivity(new Intent(Settings.ACTION_SETTINGS));
	            }
	        });
	
	        // sets "No" Button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// finishes the activity
	            	dialog.cancel();
	            }
	        });
		}

        // shows dialog box
        alertDialog.show();
	}
}
