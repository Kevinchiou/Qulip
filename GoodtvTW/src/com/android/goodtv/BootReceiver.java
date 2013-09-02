package com.android.goodtv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	SharedPreferences preferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){		     	
        	Intent i = new Intent(context,GoodtvTWActivity.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		context.startActivity(i);   
		
		}
	}

}
