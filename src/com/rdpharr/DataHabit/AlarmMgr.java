package com.rdpharr.DataHabit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmMgr extends BroadcastReceiver{
	public void onReceive(Context context, Intent intent) {
	    Log.d("alarmMgr", "got broadcast!");
	    startAlarm(context);
    }
	static public void startAlarm(Context context){
		Log.d("alarmMgr", "starting alarm");
	    AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("com.rdpharr.DataHabit.NotificationService");
	    i.setClass(context, NotificationService.class);
	    PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 60 * 1, pi); // Millisec * Second * Minute
	}
}
